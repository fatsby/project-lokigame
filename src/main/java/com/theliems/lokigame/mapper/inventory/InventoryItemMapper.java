package com.theliems.lokigame.mapper.inventory;

import com.theliems.lokigame.model.dto.inventory.InventoryItemDTO;
import com.theliems.lokigame.model.entity.inventory.InventoryItem;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InventoryItemMapper {
    InventoryItemDTO toDTO(InventoryItem item);

    List<InventoryItemDTO> toDTOList(List<InventoryItem> items);
}
