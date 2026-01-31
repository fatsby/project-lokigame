package com.theliems.lokigame.model.dto.economy;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRequestRequest {
    @NotNull
    private UUID playerId;
    @NotNull
    private Long amount;
    private String reason;
}
