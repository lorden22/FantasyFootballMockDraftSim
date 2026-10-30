package com.mockdraft.core.model;

public record Pick(int overall, int round, int pickInRound, int slot, String teamName, Player player) {

    /** Conventional "round.pick" label, e.g. "3.04". */
    public String label() {
        return round + "." + (pickInRound < 10 ? "0" : "") + pickInRound;
    }
}
