package com.theliems.lokigame.model.dto.equipment;

import com.theliems.lokigame.model.enums.EquipmentType;
import com.theliems.lokigame.model.enums.Rarity;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class EquipmentResponse {
    private UUID id;
    private EquipmentType equipmentType;
    private Rarity rarity;
    private Integer level;
    private List<EquipmentStatResponse> baseStats;
    private List<EquipmentStatResponse> randomStats;

    @Data
    @Builder
    public static class EquipmentStatResponse {
        private String statType;
        private Double value;
    }
}
