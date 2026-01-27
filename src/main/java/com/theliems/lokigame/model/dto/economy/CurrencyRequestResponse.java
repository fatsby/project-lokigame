package com.theliems.lokigame.model.dto.economy;

import com.theliems.lokigame.model.entity.economy.CurrencyRequest.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRequestResponse {
    private UUID id;
    private UUID playerId;
    private Long amount;
    private String reason;
    private RequestStatus status;
    private UUID reviewedBy;
    private String adminNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
