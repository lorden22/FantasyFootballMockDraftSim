package com.mockdraft.core.model;

/** Fantasy positions, with the roster limits used by both the lineup builder and the CPU strategy. */
public enum Position {
    QB(1, 2),
    RB(2, 6),
    WR(2, 6),
    TE(1, 2),
    K(1, 1),
    DST(1, 1);

    private final int starters;
    private final int maxRostered;

    Position(int starters, int maxRostered) {
        this.starters = starters;
        this.maxRostered = maxRostered;
    }

    public int starters() { return starters; }

    /** Most players of this position a CPU team will carry. */
    public int maxRostered() { return maxRostered; }

    public boolean isFlexEligible() { return this == RB || this == WR || this == TE; }

    public static Position parse(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Position is required");
        }
        String v = value.trim().toUpperCase();
        if (v.equals("D/ST") || v.equals("DEF")) {
            return DST;
        }
        return Position.valueOf(v);
    }
}
