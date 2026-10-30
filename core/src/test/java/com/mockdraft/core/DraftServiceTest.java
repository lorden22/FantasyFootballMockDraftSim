package com.mockdraft.core;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.mockdraft.core.draft.DraftState;
import com.mockdraft.core.model.DraftStatus;
import com.mockdraft.core.model.Pick;
import com.mockdraft.core.model.Player;
import com.mockdraft.core.model.Position;
import com.mockdraft.core.model.Roster;
import com.mockdraft.core.model.RosterSlot;
import com.mockdraft.core.service.ConflictException;
import com.mockdraft.core.service.DraftService;
import com.mockdraft.core.service.NotFoundException;
import com.mockdraft.core.service.ValidationException;

class DraftServiceTest {

    private MockDraftCore core;
    private DraftService drafts;

    @BeforeEach
    void setUp() {
        core = TestDb.core();
        core.auth().register("alice", "correct horse battery", null);
        core.auth().register("mallory", "another long secret", null);
        drafts = core.drafts();
    }

    @Test
    void seedsAllBundledPlayers() {
        DraftState state = drafts.create("alice", "Gridiron", 12, 1);
        assertThat(drafts.available(state)).hasSize(540);
    }

    @Test
    void fullDraftRunsToCompletionWithValidRosters() {
        DraftState state = drafts.create("alice", "Gridiron", 10, 4);
        long id = state.draft().id();
        int guard = 0;
        while (!state.isComplete()) {
            assertThat(guard++).isLessThan(100);
            state = drafts.simulateToUserTurn("alice", id);
            if (state.isUserTurn()) {
                assertThat(state.currentTurn().slot()).isEqualTo(4);
                state = drafts.autoPick("alice", id);
            }
        }

        assertThat(state.draft().status()).isEqualTo(DraftStatus.COMPLETE);
        assertThat(state.picks()).hasSize(10 * DraftService.ROUNDS);
        Set<Integer> ranks = new HashSet<>();
        for (Pick p : state.picks()) {
            assertThat(ranks.add(p.player().rank())).as("player drafted twice").isTrue();
        }
        for (int slot = 1; slot <= 10; slot++) {
            List<Pick> teamPicks = state.picksForSlot(slot);
            assertThat(teamPicks).hasSize(DraftService.ROUNDS);
            Map<Position, Integer> counts = new EnumMap<>(Position.class);
            teamPicks.forEach(p -> counts.merge(p.player().position(), 1, Integer::sum));
            for (Position pos : Position.values()) {
                assertThat(counts.getOrDefault(pos, 0)).as("slot %d %s", slot, pos).isBetween(pos.starters(), pos.maxRostered());
            }
            Roster roster = drafts.roster("alice", id, slot);
            assertThat(roster.starters()).hasSize(9).allSatisfy(s -> assertThat(s.pick()).isNotNull());
            assertThat(roster.bench()).hasSize(DraftService.ROUNDS - 9);
        }
        assertThatThrownBy(() -> drafts.simulateToUserTurn("alice", id)).isInstanceOf(ConflictException.class);
    }

    @Test
    void userPickRules() {
        DraftState state = drafts.create("alice", "Gridiron", 8, 2);
        long id = state.draft().id();
        assertThatThrownBy(() -> drafts.makeUserPick("alice", id, 1)).isInstanceOf(ConflictException.class)
                .hasMessageContaining("not your turn");

        state = drafts.simulateToUserTurn("alice", id);
        assertThat(state.picks()).hasSize(1);
        int takenRank = state.picks().get(0).player().rank();
        assertThatThrownBy(() -> drafts.makeUserPick("alice", id, takenRank)).isInstanceOf(ConflictException.class);
        assertThatThrownBy(() -> drafts.makeUserPick("alice", id, 99999)).isInstanceOf(ValidationException.class);

        Player choice = drafts.available(state).get(3);
        state = drafts.makeUserPick("alice", id, choice.rank());
        assertThat(state.picks().get(1).player()).isEqualTo(choice);
        assertThat(state.picks().get(1).slot()).isEqualTo(2);
        // snake: slot 2 picks again at overall 15 in an 8-team league
        assertThat(state.nextUserTurn().overall()).isEqualTo(15);
    }

    @Test
    void onlyOneDraftInProgressAndOwnershipIsEnforced() {
        DraftState state = drafts.create("alice", "Gridiron", 8, null);
        assertThat(state.draft().userSlot()).isBetween(1, 8);
        assertThatThrownBy(() -> drafts.create("alice", "Again", 8, 1)).isInstanceOf(ConflictException.class);

        long id = state.draft().id();
        assertThatThrownBy(() -> drafts.get("mallory", id)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> drafts.simulateToUserTurn("mallory", id)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> drafts.delete("mallory", id)).isInstanceOf(NotFoundException.class);
        assertThatThrownBy(() -> drafts.roster("mallory", id, 1)).isInstanceOf(NotFoundException.class);

        drafts.delete("alice", id);
        assertThat(drafts.list("alice")).isEmpty();
        assertThat(drafts.current("alice")).isEmpty();
    }

    @Test
    void validatesInput() {
        assertThatThrownBy(() -> drafts.create("alice", "", 8, 1)).isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> drafts.create("alice", "<script>", 8, 1)).isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> drafts.create("alice", "Team", 3, 1)).isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> drafts.create("alice", "Team", 21, 1)).isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> drafts.create("alice", "Team", 8, 9)).isInstanceOf(ValidationException.class);
        assertThatThrownBy(() -> drafts.roster("alice", 12345, 1)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void rosterBuildsLineupFromBestProjections() {
        DraftState state = drafts.create("alice", "Gridiron", 4, 1);
        long id = state.draft().id();
        state = drafts.autoPick("alice", id);
        Roster roster = drafts.roster("alice", id, 1);
        RosterSlot filled = roster.starters().stream().filter(s -> s.pick() != null).findFirst().orElseThrow();
        assertThat(filled.pick().player()).isEqualTo(state.picks().get(0).player());
        assertThat(roster.starters()).extracting(RosterSlot::label)
                .containsExactly("QB", "RB1", "RB2", "WR1", "WR2", "TE", "FLEX", "K", "DST");
    }
}
