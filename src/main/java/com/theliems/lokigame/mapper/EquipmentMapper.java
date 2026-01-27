package com.theliems.lokigame.mapper;

import com.theliems.lokigame.model.dto.equipment.EquipmentRequest;
import com.theliems.lokigame.model.dto.equipment.EquipmentResponse;
import com.theliems.lokigame.model.entity.equipment.Equipment;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {
    Equipment toEntity(EquipmentRequest dto);

    EquipmentResponse toDto(Equipment entity);

    void updateEntityFromDto(EquipmentRequest dto, @MappingTarget Equipment entity);
}
