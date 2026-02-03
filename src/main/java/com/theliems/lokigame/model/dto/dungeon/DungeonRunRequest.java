package com.theliems.lokigame.model.dto.dungeon;

import lombok.Data;

import java.util.UUID;

@Data
public class DungeonRunRequest {
    private UUID playerId;
    private java.util.List<UUID> heroIds;
    private UUID dungeonId;
}
