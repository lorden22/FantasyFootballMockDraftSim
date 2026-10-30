package com.mockdraft.core.draft;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.mockdraft.core.model.DraftTeam;
import com.mockdraft.core.model.Pick;
import com.mockdraft.core.model.Position;
import com.mockdraft.core.model.Roster;
import com.mockdraft.core.model.RosterSlot;

/**
 * Builds a starting lineup (QB, RB1, RB2, WR1, WR2, TE, FLEX, K, DST) plus bench from a team's picks.
 * Starters are the highest-projected players at each position; FLEX is the best remaining RB/WR/TE.
 */
public final class RosterBuilder {

    public static final String BENCH = "BN";

    private record SlotSpec(String label, Position position) {}

    private static final List<SlotSpec> LINEUP = List.of(
            new SlotSpec("QB", Position.QB),
            new SlotSpec("RB1", Position.RB),
            new SlotSpec("RB2", Position.RB),
            new SlotSpec("WR1", Position.WR),
            new SlotSpec("WR2", Position.WR),
            new SlotSpec("TE", Position.TE),
            new SlotSpec("FLEX", null),
            new SlotSpec("K", Position.K),
            new SlotSpec("DST", Position.DST));

    private static final Comparator<Pick> BY_PROJECTION =
            Comparator.comparingDouble((Pick p) -> p.player().projectedPoints()).reversed()
                    .thenComparingInt(p -> p.player().rank());

    private RosterBuilder() {}

    public static Roster build(DraftTeam team, List<Pick> teamPicks) {
        List<Pick> remaining = new ArrayList<>(teamPicks);
        remaining.sort(BY_PROJECTION);

        List<RosterSlot> starters = new ArrayList<>();
        double starterPoints = 0;
        for (SlotSpec spec : LINEUP) {
            Pick chosen = remaining.stream()
                    .filter(p -> spec.position() == null
                            ? p.player().position().isFlexEligible()
                            : p.player().position() == spec.position())
                    .findFirst()
                    .orElse(null);
            if (chosen != null) {
                remaining.remove(chosen);
                starterPoints += chosen.player().projectedPoints();
            }
            starters.add(new RosterSlot(spec.label(), chosen));
        }

        List<RosterSlot> bench = remaining.stream().map(p -> new RosterSlot(BENCH, p)).toList();
        return new Roster(team.slot(), team.name(), team.isUser(), starters, bench,
                Math.round(starterPoints * 10.0) / 10.0);
    }

    public static Roster build(DraftState state, int slot) {
        return build(state.team(slot), state.picksForSlot(slot));
    }
}
