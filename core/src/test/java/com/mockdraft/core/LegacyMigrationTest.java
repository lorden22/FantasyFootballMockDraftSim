package com.mockdraft.core;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;

import com.mockdraft.core.db.Database;
import com.mockdraft.core.draft.DraftState;
import com.mockdraft.core.model.DraftStatus;

class LegacyMigrationTest {

    @Test
    void convertsPre2Schema() {
        DataSource ds = TestDb.create();
        Database raw = new Database(ds);
        // Shape of the original mockDraft.sql tables
        raw.execute("CREATE TABLE users (username VARCHAR(50) PRIMARY KEY, salt CHAR(64) NOT NULL, hash_pass VARCHAR(256) NOT NULL, "
                + "recent_session_id VARCHAR(256), email VARCHAR(100))");
        raw.execute("CREATE TABLE drafts (draft_id INT AUTO_INCREMENT PRIMARY KEY, username VARCHAR(50), complete_status TINYINT DEFAULT 0, "
                + "num_teams INT NOT NULL, curr_round INT NOT NULL, curr_pick INT NOT NULL, date DATETIME)");
        raw.execute("CREATE TABLE players (player_rank INT PRIMARY KEY, name VARCHAR(100), position VARCHAR(3), predicted_score DECIMAL(5,2))");
        raw.execute("CREATE TABLE teams (team_name VARCHAR(50), draft_spot INT, draft_id INT, user_team TINYINT, team_array VARCHAR(100))");
        for (int r = 1; r <= 8; r++) {
            raw.update("INSERT INTO players VALUES (?, ?, ?, ?)", r, "Player " + r, r % 2 == 0 ? "WR" : "RB", 100 + r);
        }
        // user 'a' / password 'a' from the original seed data (salted SHA-256)
        raw.update("INSERT INTO users (username, salt, hash_pass) VALUES ('a', '7abf883fa820fa5097be8cd2f7013b51', "
                + "'ea93af66de80eb734a52a3c8671ed393631dca350cc67ccbdf6e1d3ac21e9bc3')");
        raw.update("INSERT INTO drafts (draft_id, username, complete_status, num_teams, curr_round, curr_pick, date) "
                + "VALUES (1, 'a', 1, 2, 3, 1, '2024-06-25 17:39:47')");
        raw.update("INSERT INTO teams VALUES ('Mine', 1, 1, 1, '1,4,5,'), ('CPU Team 2', 2, 1, 0, '2,3,6,')");

        MockDraftCore core = new MockDraftCore(ds, java.time.Clock.systemUTC(), 4,
                MockDraftCore.DEFAULT_SESSION_TTL, MockDraftCore.DEFAULT_RESET_TTL);

        assertThat(core.auth().authenticate("a", "a").username()).isEqualTo("a");
        // hash was upgraded to BCrypt
        assertThat(core.database().queryOne("SELECT legacy_salt FROM users WHERE username='a'", rs -> String.valueOf(rs.getString(1))))
                .contains("null");
        assertThat(core.auth().authenticate("a", "a").username()).isEqualTo("a");

        DraftState state = core.drafts().get("a", 1);
        assertThat(state.draft().status()).isEqualTo(DraftStatus.COMPLETE);
        assertThat(state.draft().userSlot()).isEqualTo(1);
        // snake order: 1.01 slot1, 1.02 slot2, 2.01 slot2, 2.02 slot1, 3.01 slot1, 3.02 slot2
        assertThat(state.picks()).extracting(p -> p.player().rank()).containsExactly(1, 2, 3, 4, 5, 6);
        assertThat(core.database().queryLong("SELECT COUNT(*) FROM players")).isEqualTo(8);
        assertThat(core.database().queryLong("SELECT COUNT(*) FROM legacy_teams")).isEqualTo(2);
    }
}
