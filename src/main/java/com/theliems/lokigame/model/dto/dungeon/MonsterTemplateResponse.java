package com.theliems.lokigame.model.dto.dungeon;

import com.theliems.lokigame.model.enums.StatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * Response DTO for MonsterTemplate.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonsterTemplateResponse {
    private UUID id;
    private String name;
    private String description;
    private Map<StatType, Double> baseStats;
    private Map<StatType, Double> statGrowthPerLevel;
}
