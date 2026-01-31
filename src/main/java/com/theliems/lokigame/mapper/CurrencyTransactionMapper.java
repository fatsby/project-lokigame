package com.theliems.lokigame.mapper;

import com.theliems.lokigame.model.dto.economy.CurrencyTransactionRequest;
import com.theliems.lokigame.model.dto.economy.CurrencyTransactionResponse;
import com.theliems.lokigame.model.entity.economy.CurrencyTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CurrencyTransactionMapper {
    @Mapping(target = "player.playerId", source = "playerId")
    @Mapping(target = "balanceAfter", ignore = true) // Calculated logic
    @Mapping(target = "timestamp", expression = "java(java.time.LocalDateTime.now())")
    CurrencyTransaction toEntity(CurrencyTransactionRequest dto);

    @Mapping(target = "playerId", source = "player.playerId")
    CurrencyTransactionResponse toDto(CurrencyTransaction entity);

    @Mapping(target = "player", ignore = true)
    void updateEntityFromDto(CurrencyTransactionRequest dto, @MappingTarget CurrencyTransaction entity);
}
