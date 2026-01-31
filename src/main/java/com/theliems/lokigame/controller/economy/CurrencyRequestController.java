package com.theliems.lokigame.controller.economy;

import com.theliems.lokigame.model.dto.economy.CurrencyRequestCreateRequest;
import com.theliems.lokigame.model.dto.economy.CurrencyRequestResponse;
import com.theliems.lokigame.model.entity.economy.CurrencyRequest;
import com.theliems.lokigame.service.economy.CurrencyRequestService;
import com.theliems.lokigame.service.player.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/currency-requests")
@RequiredArgsConstructor
public class CurrencyRequestController {

    private final CurrencyRequestService currencyRequestService;
    private final PlayerService playerService;

    @PostMapping
    public ResponseEntity<CurrencyRequestResponse> createRequest(
            @Valid @RequestBody CurrencyRequestCreateRequest request) {
        UUID playerId = playerService.getCurrentPlayer().getPlayerId();
        CurrencyRequest created = currencyRequestService.createRequest(playerId, request.getAmount(),
                request.getReason());
        return ResponseEntity.ok(mapToResponse(created));
    }

    @GetMapping("/my-requests")
    public ResponseEntity<List<CurrencyRequestResponse>> getMyRequests() {
        UUID playerId = playerService.getCurrentPlayer().getPlayerId();
        List<CurrencyRequest> requests = currencyRequestService.getPlayerRequests(playerId);
        List<CurrencyRequestResponse> responses = requests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private CurrencyRequestResponse mapToResponse(CurrencyRequest request) {
        return CurrencyRequestResponse.builder()
                .id(request.getId())
                .amount(request.getAmount())
                .reason(request.getReason())
                .status(request.getStatus()) // Pass enum directly
                .adminNotes(request.getAdminNotes())
                .createdAt(request.getAuditMetaData().getCreatedAt())
                .build();
    }
}
