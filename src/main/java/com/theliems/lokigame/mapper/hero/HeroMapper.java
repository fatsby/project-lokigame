package com.theliems.lokigame.mapper.hero;

import com.theliems.lokigame.mapper.inventory.InventoryItemMapper;
import com.theliems.lokigame.model.dto.hero.HeroResponseDTO;
import com.theliems.lokigame.model.entity.hero.Hero;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = { InventoryItemMapper.class })
public interface HeroMapper {

    /**
     * Maps Hero entity to HeroResponseDTO.
     * Note: The 'equipment' field is ignored here because it requires
     * fetching InventoryItem entities from the database. This mapping
     * should be done in the service layer after calling this method.
     */
    @Mapping(target = "equipment", ignore = true)
    HeroResponseDTO toDTO(Hero hero);

    @Mapping(target = "equipment", ignore = true)
    List<HeroResponseDTO> toDTOList(List<Hero> heroes);
}
