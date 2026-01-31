package com.theliems.lokigame.model.dto.dungeon;

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
public class DungeonResponse {
    private UUID id;
    private String name;
    private String description;
    private Integer level;
    private List<MonsterResponse> monsters;
}
