package com.theliems.lokigame.mapper;

import com.theliems.lokigame.model.dto.dungeon.MonsterRequest;
import com.theliems.lokigame.model.dto.dungeon.MonsterResponse;
import com.theliems.lokigame.model.entity.dungeon.Monster;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MonsterMapper {
    @Mapping(target = "dungeon.id", source = "dungeonId")
    Monster toEntity(MonsterRequest dto);

    @Mapping(target = "dungeonId", source = "dungeon.id")
    MonsterResponse toDto(Monster entity);

    @Mapping(target = "dungeon", ignore = true)
    void updateEntityFromDto(MonsterRequest dto, @MappingTarget Monster entity);
}
