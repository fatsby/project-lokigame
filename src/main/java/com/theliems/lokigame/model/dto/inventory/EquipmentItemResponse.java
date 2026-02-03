package com.theliems.lokigame.model.dto.inventory;

import com.theliems.lokigame.model.enums.EquipmentType;
import com.theliems.lokigame.model.enums.Rarity;
import com.theliems.lokigame.model.enums.StatType;
import lombok.*;

import java.util.Map;
import java.util.UUID;

/**
 * DTO for equipment items with equipment-specific fields.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentItemResponse extends InventoryItemResponse {

    private EquipmentType equipmentType;
    private Integer level;
    private Map<StatType, Double> baseStats;
    private Map<StatType, Double> randomStats;

    @Builder(builderMethodName = "equipmentItemBuilder")
    public EquipmentItemResponse(UUID id, UUID ownerId, Rarity rarity,
            String displayName, Map<String, Object> metadata,
            EquipmentType equipmentType, Integer level,
            Map<StatType, Double> baseStats, Map<StatType, Double> randomStats) {
        super(id, ownerId, rarity, displayName, metadata, "EQUIPMENT");
        this.equipmentType = equipmentType;
        this.level = level;
        this.baseStats = baseStats;
        this.randomStats = randomStats;
    }
}
