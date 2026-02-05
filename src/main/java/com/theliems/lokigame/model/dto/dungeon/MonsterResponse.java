package com.theliems.lokigame.model.dto.dungeon;

import com.theliems.lokigame.model.enums.StatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

/**
 * DTO for Monster response.
 * Represents a scaled monster instance in a procedurally generated dungeon.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonsterResponse {
    private UUID id;
    private String name;
    private String description;
    private UUID templateId;
    private Integer level;
    private Map<StatType, Double> stats;
}
