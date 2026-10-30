package com.mockdraft.core.draft;

import java.util.random.RandomGenerator;

/**
 * Chooses an index into a ranked candidate list, heavily favouring the top choice so CPU teams
 * mostly draft "best available" with occasional reaches.
 */
public final class WeightedPicker {

    private static final double[] CUMULATIVE = {0.75, 0.85, 0.90, 0.945, 0.975, 1.0};

    private final RandomGenerator random;

    public WeightedPicker(RandomGenerator random) {
        this.random = random;
    }

    /** Returns a 0-based index below {@code candidates} (which must be positive). */
    public int nextIndex(int candidates) {
        double roll = random.nextDouble();
        int index = 0;
        while (index < CUMULATIVE.length - 1 && roll > CUMULATIVE[index]) {
            index++;
        }
        return Math.min(index, candidates - 1);
    }
}
