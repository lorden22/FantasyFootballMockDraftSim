package com.mockdraft.core.draft;

/**
 * Snake draft arithmetic. Odd rounds run slot 1..N, even rounds run N..1.
 * All numbers are 1-based.
 */
public final class SnakeOrder {

    private SnakeOrder() {}

    public static int round(int overall, int numTeams) {
        return (overall - 1) / numTeams + 1;
    }

    public static int pickInRound(int overall, int numTeams) {
        return (overall - 1) % numTeams + 1;
    }

    /** Which team slot makes the given overall pick. */
    public static int slot(int overall, int numTeams) {
        int pickInRound = pickInRound(overall, numTeams);
        return round(overall, numTeams) % 2 == 1 ? pickInRound : numTeams - pickInRound + 1;
    }

    /** The overall pick number at which {@code slot} picks in {@code round}. */
    public static int overallFor(int round, int slot, int numTeams) {
        int pickInRound = round % 2 == 1 ? slot : numTeams - slot + 1;
        return (round - 1) * numTeams + pickInRound;
    }

    /** First overall pick {@code >= fromOverall} that belongs to {@code slot}, or -1 if none remain. */
    public static int nextOverallForSlot(int fromOverall, int slot, int numTeams, int rounds) {
        for (int r = round(fromOverall, numTeams); r <= rounds; r++) {
            int overall = overallFor(r, slot, numTeams);
            if (overall >= fromOverall) {
                return overall;
            }
        }
        return -1;
    }
}
