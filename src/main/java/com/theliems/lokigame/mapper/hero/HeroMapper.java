package com.theliems.lokigame.mapper.hero;

import com.theliems.lokigame.model.dto.hero.HeroResponseDTO;
import com.theliems.lokigame.model.entity.hero.Hero;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface HeroMapper {
    HeroResponseDTO toDTO(Hero hero);
}
