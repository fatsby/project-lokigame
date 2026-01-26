package com.theliems.lokigame.model.dto.admin;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CurrencyRequestResponse {
    private UUID id;
    private UUID playerId;
    private String playerUsername;
    private Long amount;
    private String reason;
    private String status;
    private String adminNotes;
    private LocalDateTime createdAt;
}
