package com.mockdraft.core.model;

import java.time.Instant;

/** Draft settings and lifecycle, without teams or picks. */
public record Draft(
        long id,
        String owner,
        String teamName,
        int numTeams,
        int rounds,
        int userSlot,
        DraftStatus status,
        Instant createdAt,
        Instant completedAt,
        int picksMade) {

    public int totalPicks() { return numTeams * rounds; }

    public boolean isComplete() { return status == DraftStatus.COMPLETE; }
}
