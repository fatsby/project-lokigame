package com.theliems.lokigame.model.dto.economy;

import com.theliems.lokigame.model.enums.TransactionSource;
import com.theliems.lokigame.model.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyTransactionRequest {
    private UUID playerId;
    private Long amount;
    private TransactionType type;
    private TransactionSource source;
}
