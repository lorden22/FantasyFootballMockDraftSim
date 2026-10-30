package com.mockdraft.core.draft;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import com.mockdraft.core.model.Pick;
import com.mockdraft.core.model.Player;
import com.mockdraft.core.model.Position;

/**
 * Decides which player a team takes. Used for CPU teams (with some randomness) and for the user's
 * auto-pick (always the top candidate).
 *
 * <p>Rules, in priority order:
 * <ol>
 *   <li>If the remaining picks only just cover the unfilled starting slots, take the best player at a needed position.</li>
 *   <li>Take a QB by round 8 and a TE by round 10 if the team has none.</li>
 *   <li>Otherwise choose from the best available players, skipping positions the team already has
 *       enough of and holding K/DST until the final three rounds.</li>
 * </ol>
 */
public final class DraftStrategy {

    private static final int CANDIDATE_POOL = 6;
    private static final int QB_DEADLINE_ROUND = 8;
    private static final int TE_DEADLINE_ROUND = 10;
    private static final int SPECIAL_TEAMS_ROUNDS = 3;

    private final WeightedPicker picker;

    public DraftStrategy(WeightedPicker picker) {
        this.picker = picker;
    }

    /**
     * @param available undrafted players sorted by rank (best first); must not be empty
     * @param teamPicks the picks this team has already made
     * @param round     the round being picked in
     * @param rounds    total rounds in the draft
     * @param bestOnly  if true, always take the top candidate (no randomness)
     */
    public Player choose(List<Player> available, List<Pick> teamPicks, int round, int rounds, boolean bestOnly) {
        if (available.isEmpty()) {
            throw new IllegalStateException("No players left to draft");
        }
        Map<Position, Integer> counts = new EnumMap<>(Position.class);
        for (Position p : Position.values()) {
            counts.put(p, 0);
        }
        for (Pick pick : teamPicks) {
            counts.merge(pick.player().position(), 1, Integer::sum);
        }

        int picksLeft = rounds - teamPicks.size();
        int unfilledStarters = 0;
        for (Position p : Position.values()) {
            unfilledStarters += Math.max(0, p.starters() - counts.get(p));
        }
        if (picksLeft <= unfilledStarters) {
            Player needed = firstMatching(available, p -> counts.get(p) < p.starters());
            if (needed != null) {
                return needed;
            }
        }

        if (round >= QB_DEADLINE_ROUND && counts.get(Position.QB) == 0) {
            Player qb = firstMatching(available, p -> p == Position.QB);
            if (qb != null) {
                return qb;
            }
        }
        if (round >= TE_DEADLINE_ROUND && counts.get(Position.TE) == 0) {
            Player te = firstMatching(available, p -> p == Position.TE);
            if (te != null) {
                return te;
            }
        }

        boolean specialTeamsWindow = round > rounds - SPECIAL_TEAMS_ROUNDS;
        List<Player> candidates = available.stream()
                .filter(p -> counts.get(p.position()) < p.position().maxRostered())
                .filter(p -> specialTeamsWindow || (p.position() != Position.K && p.position() != Position.DST))
                .limit(CANDIDATE_POOL)
                .toList();
        if (candidates.isEmpty()) {
            return available.get(0);
        }
        return candidates.get(bestOnly ? 0 : picker.nextIndex(candidates.size()));
    }

    private static Player firstMatching(List<Player> available, java.util.function.Predicate<Position> test) {
        for (Player p : available) {
            if (test.test(p.position())) {
                return p;
            }
        }
        return null;
    }
}
