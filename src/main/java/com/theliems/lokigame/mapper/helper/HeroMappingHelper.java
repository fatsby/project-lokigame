package com.theliems.lokigame.mapper.helper;

import com.theliems.lokigame.model.dto.hero.HeroEquipmentResponse;
import com.theliems.lokigame.model.entity.hero.HeroStats;
import com.theliems.lokigame.model.entity.inventory.EquipmentItem;
import com.theliems.lokigame.model.enums.EquipmentSlot;
import com.theliems.lokigame.model.enums.StatType;
import com.theliems.lokigame.repository.inventory.EquipmentItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Helper class for mapping Hero entities to DTOs.
 * Handles complex mappings like equipment and stats.
 */
@Component
@RequiredArgsConstructor
public class HeroMappingHelper {

    private final EquipmentItemRepository equipmentItemRepository;

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

    private HeroEquipmentResponse toEquipmentResponse(UUID equipmentItemId) {
        return equipmentItemRepository.findById(equipmentItemId)
                .map(item -> {
                    // Direct access to stats from EquipmentItem - no metadata parsing needed!
                    Map<StatType, Double> stats = new HashMap<>();
                    if (item.getBaseStats() != null) {
                        item.getBaseStats().forEach(s -> stats.merge(s.getStatType(), s.getValue(), Double::sum));
                    }
                    if (item.getRandomStats() != null) {
                        item.getRandomStats().forEach(s -> stats.merge(s.getStatType(), s.getValue(), Double::sum));
                    }

                    return HeroEquipmentResponse.builder()
                            .inventoryItemId(item.getId())
                            .equipmentId(item.getId()) // Same as inventoryItemId now
                            .name(item.getDisplayName())
                            .type(item.getEquipmentType())
                            .rarity(item.getRarity())
                            .level(item.getLevel())
                            .stats(stats)
                            .build();
                })
                .orElse(null);
    }
}
