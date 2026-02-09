package com.theliems.lokigame.mapper;

import com.theliems.lokigame.model.dto.dungeon.DungeonSeedResponse;
import com.theliems.lokigame.model.entity.dungeon.DungeonSeed;
import com.theliems.lokigame.model.entity.hero.World;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { WorldMapper.class })
public interface DungeonSeedMapper {

    @Mapping(target = "world", source = "world")
    @Mapping(target = "id", source = "entity.id")
    DungeonSeedResponse toDto(DungeonSeed entity, World world);
}
