package com.theliems.lokigame.model.dto.dungeon;

import com.theliems.lokigame.model.dto.battle.BattleLogEntry;
import com.theliems.lokigame.model.dto.leveling.LevelUpResult;
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
public class DungeonRunResponse {
    private UUID dungeonId;

    // Battle Results
    private String winner;
    private int turns;
    private List<BattleLogEntry> battleLogs;
    private long xpAwarded;
    private List<LevelUpResult> levelUpResults;

    // Rewards (null if battle lost)
    private List<DungeonReward> rewards;
}
