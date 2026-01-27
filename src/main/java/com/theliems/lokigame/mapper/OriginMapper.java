package com.theliems.lokigame.mapper;

import com.theliems.lokigame.model.dto.hero.OriginRequest;
import com.theliems.lokigame.model.dto.hero.OriginResponse;
import com.theliems.lokigame.model.entity.hero.Origin;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface OriginMapper {
    Origin toEntity(OriginRequest dto);

    OriginResponse toDto(Origin entity);

    void updateEntityFromDto(OriginRequest dto, @MappingTarget Origin entity);
}
