package com.theliems.lokigame.model.dto.dungeon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Represents a reward from a dungeon run.
 * Used for both service layer results and API responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DungeonReward {
    private String type; // GOLD, EQUIPMENT, MATERIAL
    private Long amount;
    private UUID itemId;
    private String name;
}
