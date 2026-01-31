package com.theliems.lokigame.model.dto.dungeon;

import com.theliems.lokigame.model.enums.StatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonsterResponse {
    private UUID id;
    private String name;
    private String description;
    // We can return ID or simple DTO. ID avoids circular dependency if Dungeon
    // includes Monsters.
    private UUID dungeonId;
    private Integer level;
    private Map<StatType, Double> stats;
}
