package com.theliems.lokigame.model.dto.equipment;

import com.theliems.lokigame.model.enums.EquipmentType;
import com.theliems.lokigame.model.enums.Rarity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentRequest {
    private EquipmentType equipmentType;
    private Rarity rarity;
    private Integer level;
    // Stats generation is usually internal, so maybe just basic info here
}
