package com.theliems.lokigame.mapper;

import com.theliems.lokigame.mapper.helper.HeroMappingHelper;
import com.theliems.lokigame.model.dto.hero.HeroRequest;
import com.theliems.lokigame.model.dto.hero.HeroResponse;
import com.theliems.lokigame.model.entity.hero.Hero;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring", uses = {
        HeroClassMapper.class,
        OriginMapper.class,
        WorldMapper.class,
        HeroMappingHelper.class
})
public abstract class HeroMapper {

    @Autowired
    protected HeroMappingHelper helper;

    @Mapping(target = "owner.playerId", source = "ownerId")
    @Mapping(target = "heroClass.id", source = "heroClassId")
    @Mapping(target = "originWorld.worldId", source = "worldId")
    @Mapping(target = "origin.id", source = "originId")
    public abstract Hero toEntity(HeroRequest dto);

    @Mapping(target = "ownerId", source = "owner.playerId")
    @Mapping(target = "createdAt", source = "auditMetaData.createdAt")
    @Mapping(target = "updatedAt", source = "auditMetaData.updatedAt")
    @Mapping(target = "equipment", expression = "java(helper.mapEquipment(entity.getEquipment()))")
    @Mapping(target = "baseStats", expression = "java(helper.mapHeroBaseStats(entity.getStats()))")
    @Mapping(target = "finalStats", expression = "java(helper.mapHeroFinalStats(entity.getStats()))")
    public abstract HeroResponse toDto(Hero entity);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "heroClass", ignore = true)
    @Mapping(target = "originWorld", ignore = true)
    @Mapping(target = "origin", ignore = true)
    public abstract void updateEntityFromDto(HeroRequest dto, @MappingTarget Hero entity);
}
