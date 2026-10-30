package com.mockdraft.core.repo;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.mockdraft.core.db.Database;
import com.mockdraft.core.draft.DraftState;
import com.mockdraft.core.draft.SnakeOrder;
import com.mockdraft.core.model.Draft;
import com.mockdraft.core.model.DraftStatus;
import com.mockdraft.core.model.DraftTeam;
import com.mockdraft.core.model.Pick;

public final class DraftRepository {

    private static final String DRAFT_COLUMNS =
            "d.draft_id, d.owner, d.team_name, d.num_teams, d.rounds, d.user_slot, d.status, d.created_at, d.completed_at, "
            + "(SELECT COUNT(*) FROM draft_picks p WHERE p.draft_id = d.draft_id) AS picks_made";

    private final Database db;
    private final PlayerRepository players;

    public DraftRepository(Database db, PlayerRepository players) {
        this.db = db;
        this.players = players;
    }

    public long insertDraft(String owner, String teamName, int numTeams, int rounds, int userSlot, Instant now) {
        return db.insertReturningKey(
                "INSERT INTO drafts (owner, team_name, num_teams, rounds, user_slot, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)",
                owner, teamName, numTeams, rounds, userSlot, DraftStatus.IN_PROGRESS, now);
    }

    public void insertTeams(long draftId, List<DraftTeam> teams) {
        List<Object[]> rows = new ArrayList<>();
        for (DraftTeam t : teams) {
            rows.add(new Object[] {draftId, t.slot(), t.name(), t.isUser()});
        }
        db.batch("INSERT INTO draft_teams (draft_id, slot, name, is_user) VALUES (?, ?, ?, ?)", rows);
    }

    public void insertPick(long draftId, int overall, int slot, int playerRank, Instant now) {
        db.update("INSERT INTO draft_picks (draft_id, overall, slot, player_rank, picked_at) VALUES (?, ?, ?, ?, ?)",
                draftId, overall, slot, playerRank, now);
    }

    public void markComplete(long draftId, Instant now) {
        db.update("UPDATE drafts SET status = ?, completed_at = ? WHERE draft_id = ?", DraftStatus.COMPLETE, now, draftId);
    }

    public void delete(long draftId) {
        // child rows go via ON DELETE CASCADE
        db.update("DELETE FROM drafts WHERE draft_id = ?", draftId);
    }

    /** Draft owned by {@code owner}; another user's draft is indistinguishable from a missing one. */
    public Optional<Draft> find(String owner, long draftId) {
        return db.queryOne("SELECT " + DRAFT_COLUMNS + " FROM drafts d WHERE d.draft_id = ? AND d.owner = ?",
                DraftRepository::mapDraft, draftId, owner);
    }

    /** Same as {@link #find} but row-locks the draft for the rest of the transaction. */
    public Optional<Draft> findForUpdate(String owner, long draftId) {
        db.query("SELECT draft_id FROM drafts WHERE draft_id = ? AND owner = ? FOR UPDATE", rs -> rs.getLong(1), draftId, owner);
        return find(owner, draftId);
    }

    public Optional<Draft> findInProgress(String owner) {
        return db.queryOne("SELECT " + DRAFT_COLUMNS + " FROM drafts d WHERE d.owner = ? AND d.status = ? "
                + "ORDER BY d.draft_id DESC LIMIT 1", DraftRepository::mapDraft, owner, DraftStatus.IN_PROGRESS);
    }

    public List<Draft> list(String owner) {
        return db.query("SELECT " + DRAFT_COLUMNS + " FROM drafts d WHERE d.owner = ? ORDER BY d.draft_id DESC",
                DraftRepository::mapDraft, owner);
    }

    public long count(String owner) {
        return db.queryLong("SELECT COUNT(*) FROM drafts WHERE owner = ?", owner);
    }

    public DraftState loadState(Draft draft) {
        List<DraftTeam> teams = db.query("SELECT slot, name, is_user FROM draft_teams WHERE draft_id = ? ORDER BY slot",
                rs -> new DraftTeam(rs.getInt(1), rs.getString(2), rs.getBoolean(3)), draft.id());
        Map<Integer, String> names = new HashMap<>();
        for (DraftTeam t : teams) {
            names.put(t.slot(), t.name());
        }
        int n = draft.numTeams();
        List<Pick> picks = db.query("SELECT overall, slot, player_rank FROM draft_picks WHERE draft_id = ? ORDER BY overall",
                rs -> {
                    int overall = rs.getInt(1);
                    int slot = rs.getInt(2);
                    return new Pick(overall, SnakeOrder.round(overall, n), SnakeOrder.pickInRound(overall, n), slot,
                            names.get(slot), players.get(rs.getInt(3)));
                }, draft.id());
        return new DraftState(draft, teams, picks);
    }

    private static Draft mapDraft(ResultSet rs) throws SQLException {
        return new Draft(rs.getLong("draft_id"), rs.getString("owner"), rs.getString("team_name"), rs.getInt("num_teams"),
                rs.getInt("rounds"), rs.getInt("user_slot"), DraftStatus.valueOf(rs.getString("status")),
                Database.instant(rs, "created_at"), Database.instant(rs, "completed_at"), rs.getInt("picks_made"));
    }
}
