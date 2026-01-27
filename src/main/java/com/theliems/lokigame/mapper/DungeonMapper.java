package com.theliems.lokigame.mapper;

import com.theliems.lokigame.model.dto.dungeon.DungeonRequest;
import com.theliems.lokigame.model.dto.dungeon.DungeonResponse;
import com.theliems.lokigame.model.entity.dungeon.Dungeon;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = { MonsterMapper.class })
public interface DungeonMapper {
    Dungeon toEntity(DungeonRequest dto);

    DungeonResponse toDto(Dungeon entity);

    void updateEntityFromDto(DungeonRequest dto, @MappingTarget Dungeon entity);
}
