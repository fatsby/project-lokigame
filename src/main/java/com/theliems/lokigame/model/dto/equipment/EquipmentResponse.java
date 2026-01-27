package com.theliems.lokigame.model.dto.equipment;

import com.theliems.lokigame.model.entity.equipment.EquipmentStat;
import com.theliems.lokigame.model.enums.EquipmentType;
import com.theliems.lokigame.model.enums.Rarity;
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
public class EquipmentResponse {
    private UUID id;
    private EquipmentType equipmentType;
    private Rarity rarity;
    private Integer level;
    private List<EquipmentStat> baseStats;
    private List<EquipmentStat> randomStats;
}
