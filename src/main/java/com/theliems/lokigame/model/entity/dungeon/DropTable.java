package com.theliems.lokigame.model.entity.dungeon;

import lombok.*;

/**
 * DTO for calculated dungeon rewards (not persisted).
 * All values are computed dynamically based on dungeon level and world
 * difficulty.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DropTable {

    /**
     * Base gold reward before multipliers.
     */
    @Builder.Default
    private Long baseGold = 100L;

    /**
     * Gold multiplier based on dungeon level.
     */
    @Builder.Default
    private Double goldMultiplier = 1.0;

    /**
     * Equipment drop chance (0.0 to 1.0).
     */
    @Builder.Default
    private Double equipmentDropChance = 0.3;

    /**
     * Material drop chance (0.0 to 1.0).
     */
    @Builder.Default
    private Double materialDropChance = 0.5;

    /**
     * Base XP reward for completing this dungeon.
     */
    @Builder.Default
    private Long baseXp = 50L;

    /**
     * XP multiplier based on dungeon difficulty.
     */
    @Builder.Default
    private Double xpMultiplier = 1.0;
}
