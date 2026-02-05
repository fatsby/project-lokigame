package com.theliems.lokigame.model.dto.battle;

import com.theliems.lokigame.model.dto.dungeon.DungeonResponse;
import com.theliems.lokigame.model.dto.leveling.LevelUpResult;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    // XP rewards (from leveling system)
    private long xpAwarded;
    private List<LevelUpResult> levelUpResults;
}
