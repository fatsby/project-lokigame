package com.theliems.lokigame.constant;

import java.util.Map;

/**
 * Constants for the Hero leveling system.
 * Contains XP curve formula parameters and rarity-based level caps.
 */
public final class LevelingConstants {

    private LevelingConstants() {
        // Prevent instantiation
    }

    // ===== XP Curve =====
    // Formula: xpRequired = XP_BASE * level^XP_EXPONENT

    /**
     * Base XP value for level-up calculation
     */
    public static final long XP_BASE = 100L;

    /**
     * Exponent for XP curve scaling
     */
    public static final double XP_EXPONENT = 1.5;

    // ===== Rarity Level Caps =====
    // Hero level is capped based on rarity until upgraded

    /**
     * Maximum level for each rarity tier (1-7 stars)
     */
    public static final Map<Integer, Integer> RARITY_LEVEL_CAPS = Map.of(
            1, 20,
            2, 40,
            3, 60,
            4, 80,
            5, 100,
            6, 120,
            7, 140);

    /**
     * Get max level for a given rarity. Returns 20 as default.
     */
    public static int getMaxLevel(int rarity) {
        return RARITY_LEVEL_CAPS.getOrDefault(rarity, 20);
    }
}
