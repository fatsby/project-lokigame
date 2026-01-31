package com.theliems.lokigame.model.dto.leveling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO representing the result of processing offline progression for a player.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfflineProgressionResult {

    /**
     * Total seconds the player was offline
     */
    private long offlineDurationSeconds;

    /**
     * Level-up results for each hero
     */
    private List<LevelUpResult> heroResults;

    /**
     * Total XP gained across all heroes
     */
    private long totalXpGained;

    /**
     * Total levels gained across all heroes
     */
    private int totalLevelsGained;

    /**
     * Create an empty result (e.g., first login, no offline time)
     */
    public static OfflineProgressionResult empty() {
        return OfflineProgressionResult.builder()
                .offlineDurationSeconds(0)
                .heroResults(List.of())
                .totalXpGained(0)
                .totalLevelsGained(0)
                .build();
    }
}
