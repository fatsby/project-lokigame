package com.theliems.lokigame.model.dto.dungeon;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class DungeonRunRequest {
    private List<UUID> heroIds;
    private UUID dungeonId;
}
