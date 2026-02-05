package com.theliems.lokigame.mapper;

import com.theliems.lokigame.model.dto.dungeon.DungeonResponse;
import com.theliems.lokigame.model.entity.dungeon.Dungeon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for Dungeon (transient object) to DungeonResponse DTO.
 * Note: Dungeon is now a generated object, not a JPA entity.
 */
@Mapper(componentModel = "spring", uses = { MonsterMapper.class })
public interface DungeonMapper {

    @Mapping(target = "monsters", source = "monsters")
    DungeonResponse toDto(Dungeon dungeon);
}
