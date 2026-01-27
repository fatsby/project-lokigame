package com.theliems.lokigame.mapper;

import com.theliems.lokigame.model.dto.player.PlayerRequest;
import com.theliems.lokigame.model.dto.player.PlayerResponse;
import com.theliems.lokigame.model.entity.player.Player;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PlayerMapper {
    @Mapping(target = "passwordHash", ignore = true) // Handled by service
    Player toEntity(PlayerRequest dto);

    PlayerResponse toDto(Player entity);

    @Mapping(target = "passwordHash", ignore = true)
    void updateEntityFromDto(PlayerRequest dto, @MappingTarget Player entity);
}
