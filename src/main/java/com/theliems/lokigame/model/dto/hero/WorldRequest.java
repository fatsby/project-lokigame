package com.theliems.lokigame.model.dto.hero;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorldRequest {
    private String name;
    private String description;
    private double rarityWeight;
    private double statMultiplier;
    private double dungeonDifficultyMod;
}
