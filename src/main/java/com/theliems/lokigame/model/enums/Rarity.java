package com.theliems.lokigame.model.enums;

public enum Rarity {
    COMMON(1, 2),
    RARE(2, 3),
    EPIC(3, 4),
    LEGENDARY(4, 6);

    private final int minRandomStats;
    private final int maxRandomStats;

    Rarity(int minRandomStats, int maxRandomStats) {
        this.minRandomStats = minRandomStats;
        this.maxRandomStats = maxRandomStats;
    }

    public int getMinRandomStats() {
        return minRandomStats;
    }

    public int getMaxRandomStats() {
        return maxRandomStats;
    }
}
