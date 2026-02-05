package com.theliems.lokigame.model.dto.battle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Represents a single entry in the battle log.
 * Contains action metadata and snapshots of all unit states after the action.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleLogEntry {
    private int turn;
    private String message;
    private String actionType; // e.g., "START", "ATTACK", "WIN_HEROES", "WIN_MONSTERS", "DRAW"
    private UUID attackerId;
    private UUID targetId;
    private Double damage;
    private Boolean isCritical;
    private List<BattleUnitState> heroStates;
    private List<BattleUnitState> monsterStates;
}
