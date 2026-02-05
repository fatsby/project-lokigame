package com.theliems.lokigame.infrastructure.constants;

/**
 * Constants for dungeon generation and scaling.
 * All scaling formulas are centralized here to avoid magic numbers.
 */
public final class DungeonConstants {

    private DungeonConstants() {
        // Prevent instantiation
    }

    // ========== Monster Scaling ==========
    /**
     * Compound multiplier per level for monster stats.
     * Formula: baseStat * (MONSTER_STAT_SCALING ^ level) * worldDifficultyMod
     */
    public static final double MONSTER_STAT_SCALING = 1.08; // 8% per level

    // ========== Reward Scaling ==========
    public static final long BASE_GOLD = 100L;
    public static final double GOLD_SCALING = 1.10; // 10% per level

    public static final long BASE_XP = 50L;
    public static final double XP_SCALING = 1.12; // 12% per level

    // ========== Drop Chances ==========
    public static final double BASE_EQUIP_DROP_CHANCE = 0.15;
    public static final double EQUIP_DROP_CHANCE_PER_LEVEL = 0.005;
    public static final double MAX_EQUIP_DROP_CHANCE = 0.60;

    public static final double BASE_MATERIAL_DROP_CHANCE = 0.30;
    public static final double MATERIAL_DROP_CHANCE_PER_LEVEL = 0.01;
    public static final double MAX_MATERIAL_DROP_CHANCE = 0.80;

    // ========== Dungeon Generation ==========
    public static final int MIN_MONSTERS_PER_DUNGEON = 1;
    public static final int MAX_MONSTERS_PER_DUNGEON = 3;
}
