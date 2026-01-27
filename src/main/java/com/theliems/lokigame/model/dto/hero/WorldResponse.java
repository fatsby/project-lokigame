package com.theliems.lokigame.model.dto.hero;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorldResponse {
    private UUID worldId;
    private String name;
    private String description;
    private double rarityWeight;
    private double statMultiplier;
    private double dungeonDifficultyMod;
}
