package com.theliems.lokigame.model.dto.dungeon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

/**
 * Result of a dungeon run containing rewards.
 * Used by DungeonService to return results to the facade layer.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DungeonRunResult {
    private UUID dungeonId;
    private List<DungeonReward> rewards;
}
