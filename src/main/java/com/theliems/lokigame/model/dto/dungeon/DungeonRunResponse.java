package com.theliems.lokigame.model.dto.dungeon;

import com.theliems.lokigame.model.dto.leveling.LevelUpResult;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class DungeonRunResponse {
    private UUID dungeonId;

    // Battle Results
    private String winner;
    private int turns;
    private List<BattleLogEntry> battleLogs;
    private long xpAwarded;
    private List<LevelUpResult> levelUpResults;

    // Rewards (null if battle lost)
    private List<RewardResponse> rewards;

    @Data
    @Builder
    public static class RewardResponse {
        private String type;
        private Long amount;
        private UUID equipmentId;
        private String name;
    }

    @Data
    @Builder
    public static class BattleLogEntry {
        private int turn;
        private String message;
    }
}
