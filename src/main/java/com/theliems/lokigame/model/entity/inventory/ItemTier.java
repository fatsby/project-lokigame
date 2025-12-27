package com.theliems.lokigame.model.entity.inventory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ItemTier {
    NORMAL(1.0, 0),
    RARE(1.2, 1),
    EPIC(1.5, 2),
    LEGENDARY(2.0, 3),
    GODSENT(5.0, 4);

    private final double statMultiplier;
    private final int rank; // Useful for comparison logic or crafting requirements
}
