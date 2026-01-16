package com.theliems.lokigame.model.dto.hero;

import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HeroSacrificeDTO {
    @Size(max = 10, message = "You can only sacrifice 10 heroes at once")
    @NotNull(message = "List of heroes cannot be null")
    @Valid
    private List<UUID> heroesToSacrificeIds;
}