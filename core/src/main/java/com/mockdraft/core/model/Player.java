package com.mockdraft.core.model;

/** A draftable player. {@code rank} is the overall consensus rank and the primary key. */
public record Player(int rank, String name, Position position, double projectedPoints) {

    /** The player's rank expressed as a "round.pick" slot for a league of {@code numTeams}, e.g. "2.07". */
    public String adp(int numTeams) {
        int round = (rank - 1) / numTeams + 1;
        int pick = (rank - 1) % numTeams + 1;
        return round + "." + (pick < 10 ? "0" : "") + pick;
    }
}
