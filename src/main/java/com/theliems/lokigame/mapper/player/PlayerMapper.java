package com.theliems.lokigame.mapper.player;

import com.theliems.lokigame.model.entity.player.Player;
import org.mapstruct.Mapper;
import com.theliems.lokigame.model.dto.player.PlayerResponseDTO;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PlayerMapper {
    @Mapping(target = "active", source = "auditMetaData.active")
    @Mapping(target = "deleted", source = "auditMetaData.deleted")
    @Mapping(target = "createdAt", source = "auditMetaData.createdAt")
    @Mapping(target = "updatedAt", source = "auditMetaData.updatedAt")
    @Mapping(target = "createdBy", source = "auditMetaData.createdBy")
    @Mapping(target = "updatedBy", source = "auditMetaData.updatedBy")
    PlayerResponseDTO toDTO(Player player);
}
