package com.theliems.lokigame.model.entity.world;

import lombok.Data;

@Data
public class WorldDefinition {
    private String id;
    private String name;
    private String description;

    // For Hero Generation RNG
    private double rarityWeight; // Higher = More Common (e.g., 100 vs 5)

    // The "Bonus" logic
    private double statMultiplier;

    // For Future Phase: Dungeon Scaling
    private double dungeonDifficultyMod;
}