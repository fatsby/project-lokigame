package com.theliems.lokigame.model.dto.equipment;

import com.theliems.lokigame.model.enums.EquipmentType;
import lombok.Data;

@Data
public class EquipmentGenerateRequest {
    private EquipmentType equipmentType;
    private Integer playerLevel;
    private Integer dungeonLevel;
}
