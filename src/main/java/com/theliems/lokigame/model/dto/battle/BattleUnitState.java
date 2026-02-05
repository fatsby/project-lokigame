package com.theliems.lokigame.model.dto.battle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Represents the state of a battle unit (hero or monster) at a specific point
 * in battle.
 * Used for tracking HP and status throughout battle simulation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleUnitState {
    private UUID id;
    private String name;
    private double maxHp;
    private double currentHp;
    private boolean isHero;
    private boolean isAlive;
}
