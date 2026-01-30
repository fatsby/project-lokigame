package com.theliems.lokigame.model.dto.hero;

import com.theliems.lokigame.model.enums.EquipmentSlot;
import com.theliems.lokigame.model.enums.HeroGender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeroResponse {
    private UUID heroId;
    private UUID ownerId;
    private HeroClassResponse heroClass;
    private HeroGender gender;
    private String firstName;
    private String lastName;
    private WorldResponse originWorld;
    private OriginResponse origin;
    private Integer level;
    private Integer star;
    private Long experience;
    private Double willPower;
    private Double expPerSecond;
    private java.util.Map<com.theliems.lokigame.model.enums.StatType, Double> baseStats;
    private java.util.Map<com.theliems.lokigame.model.enums.StatType, Double> finalStats;
    private Map<EquipmentSlot, HeroEquipmentResponse> equipment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
