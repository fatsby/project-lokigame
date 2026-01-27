package com.theliems.lokigame.model.enums;

public enum Rarity {
    COMMON(1, 2, ItemTier.NORMAL),
    RARE(2, 3, ItemTier.RARE),
    EPIC(3, 4, ItemTier.EPIC),
    LEGENDARY(4, 6, ItemTier.LEGENDARY),
    GODSENT(0, 0, ItemTier.GODSENT); // Godsent items have fixed stats, so min/max random stats are 0

    private final int minRandomStats;
    private final int maxRandomStats;
    private final ItemTier itemTier;

    Rarity(int minRandomStats, int maxRandomStats, ItemTier itemTier) {
        this.minRandomStats = minRandomStats;
        this.maxRandomStats = maxRandomStats;
        this.itemTier = itemTier;
    }

    public int getMinRandomStats() {
        return minRandomStats;
    }

    public int getMaxRandomStats() {
        return maxRandomStats;
    }

    public ItemTier toItemTier() {
        return this.itemTier;
    }
}
