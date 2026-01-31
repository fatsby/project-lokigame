package com.theliems.lokigame.model.dto.hero;

import com.theliems.lokigame.model.enums.HeroGender;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeroRequest {
    @NotNull
    private UUID ownerId;
    @NotNull
    private UUID heroClassId;
    @NotNull
    private HeroGender gender;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    private Integer rarity;
    private UUID worldId;
    private UUID originId;
}
