package com.theliems.lokigame.model.dto.hero;

import com.theliems.lokigame.model.enums.EquipmentSlot;
import com.theliems.lokigame.model.enums.HeroGender;
import lombok.Builder;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class HeroResponseDTO {
    private UUID id;
    private String heroClass;
    private HeroGender gender;
    private String firstName;
    private String lastName;
    private int rarity;
    private String originWorldId;
    private int level;
    private long experience;
    private double willPower;
    private double expPerSecond;
    private Map<String, Double> stats;
    private Map<String, String> visuals;
    private Map<EquipmentSlot, UUID> equipment;
}
