package com.mockdraft.core.draft;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.mockdraft.core.model.Draft;
import com.mockdraft.core.model.DraftTeam;
import com.mockdraft.core.model.Pick;

/** A full snapshot of a draft: settings, teams and every pick made so far, plus derived turn information. */
public final class DraftState {

    /** Where the draft is right now. */
    public record Turn(int overall, int round, int pickInRound, int slot, String teamName, boolean isUser) {}

    /** The user's next pick and how many picks happen before it (0 = on the clock). */
    public record UserTurn(int overall, int round, int pickInRound, int picksUntil) {}

    private final Draft draft;
    private final List<DraftTeam> teams;
    private final List<Pick> picks;

    public DraftState(Draft draft, List<DraftTeam> teams, List<Pick> picks) {
        this.draft = draft;
        this.teams = List.copyOf(teams);
        this.picks = List.copyOf(picks);
    }

    public Draft draft() { return draft; }
    public List<DraftTeam> teams() { return teams; }
    public List<Pick> picks() { return picks; }

    public boolean isComplete() {
        return draft.isComplete() || picks.size() >= draft.totalPicks();
    }

    public DraftTeam team(int slot) {
        return teams.stream().filter(t -> t.slot() == slot).findFirst()
                .orElseThrow(() -> new IllegalStateException("No team in slot " + slot));
    }

    /** The pick on the clock, or null once the draft is over. */
    public Turn currentTurn() {
        if (isComplete()) {
            return null;
        }
        int overall = picks.size() + 1;
        int n = draft.numTeams();
        int slot = SnakeOrder.slot(overall, n);
        DraftTeam team = team(slot);
        return new Turn(overall, SnakeOrder.round(overall, n), SnakeOrder.pickInRound(overall, n), slot, team.name(), team.isUser());
    }

    public boolean isUserTurn() {
        Turn turn = currentTurn();
        return turn != null && turn.isUser();
    }

    public UserTurn nextUserTurn() {
        if (isComplete()) {
            return null;
        }
        int from = picks.size() + 1;
        int n = draft.numTeams();
        int overall = SnakeOrder.nextOverallForSlot(from, draft.userSlot(), n, draft.rounds());
        if (overall < 0) {
            return null;
        }
        return new UserTurn(overall, SnakeOrder.round(overall, n), SnakeOrder.pickInRound(overall, n), overall - from);
    }

    public Set<Integer> draftedRanks() {
        Set<Integer> ranks = new HashSet<>();
        for (Pick p : picks) {
            ranks.add(p.player().rank());
        }
        return Collections.unmodifiableSet(ranks);
    }

    public List<Pick> picksForSlot(int slot) {
        return picks.stream().filter(p -> p.slot() == slot).toList();
    }
}
