package com.theliems.lokigame.model.dto.battle;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class BattleSimulateResponse {
    private String winner;
    private int turns;
    private List<BattleLogEntry> logs;

    @Data
    @Builder
    public static class BattleLogEntry {
        private int turn;
        private String message;
    }
}
