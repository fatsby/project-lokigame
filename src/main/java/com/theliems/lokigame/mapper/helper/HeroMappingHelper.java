package com.theliems.lokigame.mapper.helper;

import com.theliems.lokigame.model.dto.hero.HeroEquipmentResponse;
import com.theliems.lokigame.model.entity.equipment.Equipment;
import com.theliems.lokigame.model.entity.hero.HeroStats;
import com.theliems.lokigame.model.enums.EquipmentSlot;
import com.theliems.lokigame.model.enums.ItemType;
import com.theliems.lokigame.model.enums.Rarity;
import com.theliems.lokigame.model.enums.StatType;
import com.theliems.lokigame.repository.equipment.EquipmentRepository;
import com.theliems.lokigame.repository.inventory.InventoryItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class HeroMappingHelper {

    private final InventoryItemRepository inventoryItemRepository;
    private final EquipmentRepository equipmentRepository;

    public Map<StatType, Double> mapHeroBaseStats(List<HeroStats> stats) {
        if (stats == null)
            return new HashMap<>();
        Map<StatType, Double> result = new HashMap<>();
        for (HeroStats stat : stats) {
            result.put(stat.getStatType(), stat.getBaseValue());
        }
        return result;
    }

    public Map<StatType, Double> mapHeroFinalStats(List<HeroStats> stats) {
        if (stats == null)
            return new HashMap<>();
        Map<StatType, Double> result = new HashMap<>();
        for (HeroStats stat : stats) {
            result.put(stat.getStatType(), stat.getFinalValue());
        }
        return result;
    }

    public Map<EquipmentSlot, HeroEquipmentResponse> mapEquipment(Map<EquipmentSlot, UUID> equipmentMap) {
        if (equipmentMap == null)
            return null;

        Map<EquipmentSlot, HeroEquipmentResponse> result = new HashMap<>();
        for (Map.Entry<EquipmentSlot, UUID> entry : equipmentMap.entrySet()) {
            if (entry.getValue() != null) {
                result.put(entry.getKey(), toEquipmentResponse(entry.getValue()));
            }
        }
        return result;
    }

    private HeroEquipmentResponse toEquipmentResponse(UUID inventoryItemId) {
        return inventoryItemRepository.findById(inventoryItemId)
                .map(item -> {
                    Equipment equipmentDef = null;
                    if (item.getMetadata() != null && item.getMetadata().containsKey("equipmentId")) {
                        try {
                            UUID equipmentDefId = UUID.fromString((String) item.getMetadata().get("equipmentId"));
                            equipmentDef = equipmentRepository.findById(equipmentDefId).orElse(null);
                        } catch (Exception e) {
                            // ignore
                        }
                    }

                    String name = (equipmentDef != null) ? equipmentDef.getEquipmentType().name() : "Unknown Item";

                    Map<StatType, Double> stats = new HashMap<>();
                    if (equipmentDef != null) {
                        if (equipmentDef.getBaseStats() != null)
                            equipmentDef.getBaseStats()
                                    .forEach(s -> stats.merge(s.getStatType(), s.getValue(), Double::sum));
                        if (equipmentDef.getRandomStats() != null)
                            equipmentDef.getRandomStats()
                                    .forEach(s -> stats.merge(s.getStatType(), s.getValue(), Double::sum));
                    }

                    return HeroEquipmentResponse.builder()
                            .inventoryItemId(item.getId())
                            .equipmentId(equipmentDef != null ? equipmentDef.getId() : null)
                            .name(name)
                            .type(item.getType() == ItemType.EQUIPMENT
                                    ? (equipmentDef != null ? equipmentDef.getEquipmentType() : null)
                                    : null)
                            .rarity(mapRarity(item.getTier()))
                            .level(equipmentDef != null ? equipmentDef.getLevel() : 1)
                            .stats(stats)
                            .build();
                })
                .orElse(null);
    }

    private Rarity mapRarity(com.theliems.lokigame.model.enums.ItemTier tier) {
        if (tier == null)
            return null;
        if (tier == com.theliems.lokigame.model.enums.ItemTier.NORMAL)
            return Rarity.COMMON;
        try {
            return Rarity.valueOf(tier.name());
        } catch (IllegalArgumentException e) {
            return Rarity.COMMON; // Fallback
        }
    }
}
