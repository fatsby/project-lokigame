package com.theliems.lokigame.controller.admin;

import com.theliems.lokigame.model.dto.admin.CurrencyRequestApproveRequest;
import com.theliems.lokigame.model.dto.admin.CurrencyRequestResponse;
import com.theliems.lokigame.model.entity.economy.CurrencyRequest;
import com.theliems.lokigame.service.economy.CurrencyRequestService;
import com.theliems.lokigame.service.player.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final CurrencyRequestService currencyRequestService;
    private final PlayerService playerService;

    @GetMapping("/currency-requests/pending")
    public ResponseEntity<List<CurrencyRequestResponse>> getPendingRequests() {
        List<CurrencyRequest> requests = currencyRequestService.getPendingRequests();
        List<CurrencyRequestResponse> responses = requests.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/currency-requests/{requestId}/approve")
    public ResponseEntity<CurrencyRequestResponse> approveRequest(
            @PathVariable UUID requestId,
            @Valid @RequestBody CurrencyRequestApproveRequest request) {
        UUID adminId = playerService.getCurrentPlayer().getPlayerId();
        CurrencyRequest approved = currencyRequestService.approveRequest(requestId, adminId, request.getNotes());
        return ResponseEntity.ok(mapToResponse(approved));
    }

    @PostMapping("/currency-requests/{requestId}/reject")
    public ResponseEntity<CurrencyRequestResponse> rejectRequest(
            @PathVariable UUID requestId,
            @Valid @RequestBody CurrencyRequestApproveRequest request) {
        UUID adminId = playerService.getCurrentPlayer().getPlayerId();
        CurrencyRequest rejected = currencyRequestService.rejectRequest(requestId, adminId, request.getNotes());
        return ResponseEntity.ok(mapToResponse(rejected));
    }

    private CurrencyRequestResponse mapToResponse(CurrencyRequest request) {
        return CurrencyRequestResponse.builder()
                .id(request.getId())
                .playerId(request.getPlayer().getPlayerId())
                .playerUsername(request.getPlayer().getUsername())
                .amount(request.getAmount())
                .reason(request.getReason())
                .status(request.getStatus().name())
                .adminNotes(request.getAdminNotes())
                .createdAt(request.getAuditMetaData().getCreatedAt())
                .build();
    }
}
