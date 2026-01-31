package com.theliems.lokigame.model.dto.hero;

import com.theliems.lokigame.model.enums.EquipmentType;
import com.theliems.lokigame.model.enums.Rarity;
import com.theliems.lokigame.model.enums.StatType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeroEquipmentResponse {
    private UUID inventoryItemId;
    private UUID equipmentId; // The underlying instance ID
    private String name; // Name from metadata or equipment lookup?
    private EquipmentType type;
    private Rarity rarity;
    private Integer level;
    private Map<StatType, Double> stats; // Aggregated stats for display
}
