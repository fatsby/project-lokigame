package com.theliems.lokigame.model.dto.battle;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BattleSimulateRequest {
    private List<UUID> heroIds;
    private UUID dungeonId;
}
