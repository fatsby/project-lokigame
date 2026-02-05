package com.theliems.lokigame.model.entity.dungeon;

import com.theliems.lokigame.model.enums.StatType;
import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Transient scaled monster instance (not persisted).
 * Generated procedurally from MonsterTemplate at a specific dungeon level.
 * Used by BattleService for combat simulation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Monster {

    /**
     * Unique identifier for this monster instance within the dungeon.
     */
    private UUID id;

    /**
     * Display name of the monster.
     */
    private String name;

    /**
     * Optional description/flavor text.
     */
    private String description;

    /**
     * Reference to the MonsterTemplate this was scaled from.
     */
    private UUID templateId;

    /**
     * The level this monster was scaled to.
     */
    private Integer level;

    /**
     * Final calculated stats after scaling.
     * Format: {HP: 1000.0, ATK: 100.0, DEF: 50.0, SPEED: 80.0, CRIT_RATE: 0.1,
     * CRIT_DAMAGE: 1.5}
     */
    @Builder.Default
    private Map<StatType, Double> stats = new HashMap<>();
}
