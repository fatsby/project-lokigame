package com.theliems.lokigame.model.dto.dungeon;

import com.theliems.lokigame.model.dto.hero.WorldResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DungeonSeedResponse {
    private UUID id;
    private UUID playerId;
    private WorldResponse world;
    private Integer dungeonLevel;
    private Long seed;
    private Boolean cleared;
    private String dungeonName;
}
