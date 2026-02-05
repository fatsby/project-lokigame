package com.theliems.lokigame.mapper;

import com.theliems.lokigame.model.dto.dungeon.MonsterResponse;
import com.theliems.lokigame.model.entity.dungeon.Monster;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for Monster (transient object) to MonsterResponse DTO.
 * Note: Monster is now a generated object, not a JPA entity.
 * There is no MonsterRequest mapping since monsters are procedurally generated.
 */
@Mapper(componentModel = "spring")
public interface MonsterMapper {

    @Mapping(target = "templateId", source = "templateId")
    MonsterResponse toDto(Monster monster);
}
