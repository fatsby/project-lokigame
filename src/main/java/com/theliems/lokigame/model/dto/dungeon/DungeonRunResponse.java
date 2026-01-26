package com.theliems.lokigame.model.dto.dungeon;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class DungeonRunResponse {
    private UUID dungeonId;
    private List<RewardResponse> rewards;

    @Data
    @Builder
    public static class RewardResponse {
        private String type;
        private Long amount;
        private UUID equipmentId;
    }
}
