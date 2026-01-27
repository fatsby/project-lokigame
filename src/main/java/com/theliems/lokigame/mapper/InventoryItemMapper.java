package com.theliems.lokigame.mapper;

import com.theliems.lokigame.model.dto.inventory.InventoryItemRequest;
import com.theliems.lokigame.model.dto.inventory.InventoryItemResponse;
import com.theliems.lokigame.model.entity.inventory.InventoryItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface InventoryItemMapper {
    @Mapping(target = "owner.playerId", source = "ownerId")
    InventoryItem toEntity(InventoryItemRequest dto);

    @Mapping(target = "ownerId", source = "owner.playerId")
    InventoryItemResponse toDto(InventoryItem entity);

    @Mapping(target = "owner", ignore = true)
    void updateEntityFromDto(InventoryItemRequest dto, @MappingTarget InventoryItem entity);
}
