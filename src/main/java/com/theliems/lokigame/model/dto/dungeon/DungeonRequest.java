package com.theliems.lokigame.model.dto.dungeon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DungeonRequest {
    private String name;
    private String description;
    private Integer level;
    // Monsters are usually added separately or via child DTOs, but typically
    // separate endpoints for management.
}
