package com.theliems.lokigame.model.dto.dungeon;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for initiating a dungeon run.
 * Uses procedural generation based on level and world.
 */
@Data
public class DungeonRunRequest {
    private List<UUID> heroIds;

    /**
     * The dungeon level to attempt.
     * Must be <= player's highest cleared level + 1
     */
    private Integer dungeonLevel;
}
