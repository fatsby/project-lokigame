package com.theliems.lokigame.mapper;

import com.theliems.lokigame.model.dto.hero.WorldRequest;
import com.theliems.lokigame.model.dto.hero.WorldResponse;
import com.theliems.lokigame.model.entity.hero.World;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface WorldMapper {
    World toEntity(WorldRequest dto);

    WorldResponse toDto(World entity);

    void updateEntityFromDto(WorldRequest dto, @MappingTarget World entity);
}
