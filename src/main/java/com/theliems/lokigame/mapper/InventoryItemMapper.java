package com.theliems.lokigame.mapper;

import com.theliems.lokigame.model.dto.inventory.EquipmentItemResponse;
import com.theliems.lokigame.model.dto.inventory.InventoryItemResponse;
import com.theliems.lokigame.model.entity.equipment.EquipmentStat;
import com.theliems.lokigame.model.entity.inventory.EquipmentItem;
import com.theliems.lokigame.model.entity.inventory.InventoryItem;
import com.theliems.lokigame.model.enums.StatType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mapper for InventoryItem hierarchy using MapStruct polymorphic mapping.
 */
@Mapper(componentModel = "spring")
public interface InventoryItemMapper {

    @SubclassMapping(source = EquipmentItem.class, target = EquipmentItemResponse.class)
    @Mapping(target = "ownerId", source = "owner.playerId")
    @Mapping(target = "displayName", expression = "java(entity.getDisplayName())")
    InventoryItemResponse toDto(InventoryItem entity);

    @Mapping(target = "ownerId", source = "owner.playerId")
    @Mapping(target = "displayName", expression = "java(entity.getDisplayName())")
    @Mapping(target = "baseStats", source = "baseStats", qualifiedByName = "statsToMap")
    @Mapping(target = "randomStats", source = "randomStats", qualifiedByName = "statsToMap")
    EquipmentItemResponse toEquipmentDto(EquipmentItem entity);

    @Named("statsToMap")
    default Map<StatType, Double> statsToMap(List<EquipmentStat> stats) {
        if (stats == null)
            return new HashMap<>();
        Map<StatType, Double> result = new HashMap<>();
        for (EquipmentStat stat : stats) {
            result.put(stat.getStatType(), stat.getValue());
        }
        return result;
    }

    /**
     * After mapping hook for setting itemCategory.
     */
    default void setItemCategory(EquipmentItemResponse response) {
        if (response != null) {
            response.setItemCategory("EQUIPMENT");
        }
    }
}
