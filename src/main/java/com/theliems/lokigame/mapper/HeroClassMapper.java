package com.theliems.lokigame.mapper;

import com.theliems.lokigame.model.dto.hero.HeroClassRequest;
import com.theliems.lokigame.model.dto.hero.HeroClassResponse;
import com.theliems.lokigame.model.entity.hero.HeroClass;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface HeroClassMapper {
    HeroClass toEntity(HeroClassRequest dto);

    HeroClassResponse toDto(HeroClass entity);

    void updateEntityFromDto(HeroClassRequest dto, @MappingTarget HeroClass entity);
}
