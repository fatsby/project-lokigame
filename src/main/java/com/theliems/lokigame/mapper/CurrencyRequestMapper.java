package com.theliems.lokigame.mapper;

import com.theliems.lokigame.model.dto.economy.CurrencyRequestRequest;
import com.theliems.lokigame.model.dto.economy.CurrencyRequestResponse;
import com.theliems.lokigame.model.entity.economy.CurrencyRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CurrencyRequestMapper {
    @Mapping(target = "player.playerId", source = "playerId")
    @Mapping(target = "status", constant = "PENDING")
    CurrencyRequest toEntity(CurrencyRequestRequest dto);

    @Mapping(target = "playerId", source = "player.playerId")
    @Mapping(target = "reviewedBy", source = "reviewedBy.playerId")
    @Mapping(target = "createdAt", source = "auditMetaData.createdAt")
    @Mapping(target = "updatedAt", source = "auditMetaData.updatedAt")
    CurrencyRequestResponse toDto(CurrencyRequest entity);

    @Mapping(target = "player", ignore = true)
    @Mapping(target = "status", ignore = true) // Status changes via specific business logic usually
    void updateEntityFromDto(CurrencyRequestRequest dto, @MappingTarget CurrencyRequest entity);
}
