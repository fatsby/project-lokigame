package com.theliems.lokigame.model.dto.battle;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for battle simulation.
 * Uses procedural dungeon generation based on level and world.
 */
@Data
public class BattleSimulateRequest {
    private List<UUID> heroIds;
    private Integer dungeonLevel;
    private UUID worldId;
}
