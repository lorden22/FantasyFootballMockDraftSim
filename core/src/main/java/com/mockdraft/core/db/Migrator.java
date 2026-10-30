package com.mockdraft.core.db;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mockdraft.core.draft.SnakeOrder;
import com.mockdraft.core.model.DraftStatus;
import com.mockdraft.core.model.Player;

/**
 * Brings any database up to the current schema. Both apps call {@link #migrate()} on startup.
 *
 * <ul>
 *   <li>Applies versioned SQL scripts once each, tracked in {@code schema_version}.</li>
 *   <li>Detects the pre-2.0 schema (comma-separated {@code teams.team_array}), renames those tables to
 *       {@code legacy_*}, and copies users, players and drafts into the new schema. The legacy tables
 *       are kept so nothing is lost; drop them manually once satisfied.</li>
 *   <li>Seeds the {@code players} table from the bundled rankings when it is empty.</li>
 * </ul>
 */
public final class Migrator {

    private static final Logger log = LoggerFactory.getLogger(Migrator.class);

    private record Migration(int version, String description, String resource) {}

    private static final List<Migration> MIGRATIONS = List.of(
            new Migration(1, "initial schema", "/com/mockdraft/core/db/V1__schema.sql"));

    /** Rounds used for drafts converted from the legacy schema (which always ran 15 rounds). */
    private static final int LEGACY_ROUNDS = 15;

    private final Database db;

    public Migrator(Database db) {
        this.db = db;
    }

    public void migrate() {
        db.execute("CREATE TABLE IF NOT EXISTS schema_version ("
                + "version INT NOT NULL PRIMARY KEY, description VARCHAR(200) NOT NULL, applied_at DATETIME NOT NULL)");

        boolean legacy = !isApplied(1) && isLegacySchema();
        if (legacy) {
            log.warn("Pre-2.0 schema detected; converting to the new schema (old tables are kept as legacy_*)");
            for (String table : new String[] {"users", "drafts", "teams", "players"}) {
                db.execute("ALTER TABLE " + table + " RENAME TO legacy_" + table);
            }
        }

        for (Migration m : MIGRATIONS) {
            if (!isApplied(m.version())) {
                log.info("Applying schema migration V{} ({})", m.version(), m.description());
                for (String statement : statements(m.resource())) {
                    db.execute(statement);
                }
                db.update("INSERT INTO schema_version (version, description, applied_at) VALUES (?, ?, ?)",
                        m.version(), m.description(), Instant.now());
            }
        }

        if (legacy) {
            db.inTransaction(this::copyLegacyData);
        }
        seedPlayersIfEmpty();
    }

    private boolean isApplied(int version) {
        return db.queryLong("SELECT COUNT(*) FROM schema_version WHERE version = ?", version) > 0;
    }

    private boolean isLegacySchema() {
        return db.withConnection(conn -> {
            DatabaseMetaData meta = conn.getMetaData();
            for (String table : new String[] {"teams", "TEAMS"}) {
                try (ResultSet rs = meta.getColumns(conn.getCatalog(), null, table, null)) {
                    while (rs.next()) {
                        if ("team_array".equalsIgnoreCase(rs.getString("COLUMN_NAME"))) {
                            return true;
                        }
                    }
                }
            }
            return false;
        });
    }

    private boolean hasColumn(String table, String column) {
        return db.withConnection(conn -> {
            try (ResultSet rs = conn.getMetaData().getColumns(conn.getCatalog(), null, table, null)) {
                while (rs.next()) {
                    if (column.equalsIgnoreCase(rs.getString("COLUMN_NAME"))) {
                        return true;
                    }
                }
            }
            return false;
        });
    }

    private void seedPlayersIfEmpty() {
        if (db.queryLong("SELECT COUNT(*) FROM players") > 0) {
            return;
        }
        List<Player> players = PlayerDataLoader.loadBundled();
        List<Object[]> rows = new ArrayList<>();
        for (Player p : players) {
            rows.add(new Object[] {p.rank(), p.name(), p.position().name(), p.projectedPoints()});
        }
        db.batch("INSERT INTO players (player_rank, name, position, projected_points) VALUES (?, ?, ?, ?)", rows);
        log.info("Seeded {} players from bundled rankings", players.size());
    }

    private void copyLegacyData() {
        Instant now = Instant.now();
        String emailColumn = hasColumn("legacy_users", "email") ? "email" : "NULL";
        db.update("INSERT INTO users (username, email, password_hash, legacy_salt, created_at) "
                + "SELECT username, " + emailColumn + ", hash_pass, CASE WHEN salt = 'BCRYPT' THEN NULL ELSE salt END, ? "
                + "FROM legacy_users", now);
        db.update("INSERT INTO players (player_rank, name, position, projected_points) "
                + "SELECT player_rank, name, position, predicted_score FROM legacy_players");

        record LegacyTeam(int slot, String name, boolean isUser, String teamArray) {}
        Map<Long, List<LegacyTeam>> teamsByDraft = new HashMap<>();
        db.query("SELECT draft_id, draft_spot, team_name, user_team, team_array FROM legacy_teams", rs -> {
            teamsByDraft.computeIfAbsent(rs.getLong("draft_id"), k -> new ArrayList<>())
                    .add(new LegacyTeam(rs.getInt("draft_spot"), rs.getString("team_name"),
                            rs.getInt("user_team") == 1, rs.getString("team_array")));
            return null;
        });
        Set<Integer> knownRanks = Set.copyOf(db.query("SELECT player_rank FROM players", rs -> rs.getInt(1)));

        int converted = 0;
        for (Object[] d : db.query("SELECT draft_id, username, complete_status, num_teams, date FROM legacy_drafts",
                rs -> new Object[] {rs.getLong(1), rs.getString(2), rs.getInt(3), rs.getInt(4), Database.instant(rs, "date")})) {
            long draftId = (long) d[0];
            String owner = (String) d[1];
            int numTeams = (int) d[3];
            Instant created = d[4] == null ? now : (Instant) d[4];
            List<LegacyTeam> teams = teamsByDraft.getOrDefault(draftId, List.of());
            LegacyTeam userTeam = teams.stream().filter(LegacyTeam::isUser).findFirst().orElse(null);
            if (owner == null || userTeam == null || teams.size() != numTeams || numTeams < 2 || numTeams > 20) {
                log.warn("Skipping malformed legacy draft {}", draftId);
                continue;
            }
            DraftStatus status = (int) d[2] == 1 ? DraftStatus.COMPLETE : DraftStatus.IN_PROGRESS;
            db.update("INSERT INTO drafts (draft_id, owner, team_name, num_teams, rounds, user_slot, status, created_at, completed_at) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    draftId, owner, userTeam.name(), numTeams, LEGACY_ROUNDS, userTeam.slot(), status, created,
                    status == DraftStatus.COMPLETE ? created : null);
            List<Object[]> teamRows = new ArrayList<>();
            List<Object[]> pickRows = new ArrayList<>();
            for (LegacyTeam t : teams) {
                teamRows.add(new Object[] {draftId, t.slot(), t.name(), t.isUser()});
                if (t.teamArray() == null) {
                    continue;
                }
                int round = 1;
                for (String token : t.teamArray().split(",")) {
                    if (token.isBlank() || round > LEGACY_ROUNDS) {
                        continue;
                    }
                    int rank = Integer.parseInt(token.trim());
                    if (knownRanks.contains(rank)) {
                        pickRows.add(new Object[] {draftId, SnakeOrder.overallFor(round, t.slot(), numTeams), t.slot(), rank, created});
                    }
                    round++;
                }
            }
            db.batch("INSERT INTO draft_teams (draft_id, slot, name, is_user) VALUES (?, ?, ?, ?)", teamRows);
            db.batch("INSERT INTO draft_picks (draft_id, overall, slot, player_rank, picked_at) VALUES (?, ?, ?, ?, ?)", pickRows);
            converted++;
        }
        log.info("Converted {} legacy drafts", converted);
    }

    private static List<String> statements(String resource) {
        try (InputStream in = Migrator.class.getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalStateException("Missing migration " + resource);
            }
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            List<String> result = new ArrayList<>();
            StringBuilder current = new StringBuilder();
            for (String line : sql.split("\n")) {
                if (line.strip().startsWith("--")) {
                    continue;
                }
                current.append(line).append('\n');
                if (line.strip().endsWith(";")) {
                    String stmt = current.toString().strip();
                    result.add(stmt.substring(0, stmt.length() - 1));
                    current.setLength(0);
                }
            }
            if (!current.toString().isBlank()) {
                result.add(current.toString().strip());
            }
            return result;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
