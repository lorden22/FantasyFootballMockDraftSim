package com.mockdraft.core.model;

import java.util.List;

public record Roster(
        int slot,
        String teamName,
        boolean isUser,
        List<RosterSlot> starters,
        List<RosterSlot> bench,
        double projectedStarterPoints) {}
