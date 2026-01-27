package com.theliems.lokigame.model.dto.battle;

import com.theliems.lokigame.model.dto.dungeon.DungeonResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BattleSimulateResponse {
    private String winner;
    private Integer turns;
    private List<BattleLogEntry> logs;

    // Requested info
    private List<BattleUnitState> heroes; // Final state of heroes
    private DungeonResponse dungeon; // Info about dungeon
    private List<BattleUnitState> monsters;// Final state of monsters

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BattleLogEntry {
        private int turn;
        private String message;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BattleUnitState {
        private UUID id;
        private String name;
        private double maxHp;
        private double currentHp;
        private boolean isHero;
    }
}
