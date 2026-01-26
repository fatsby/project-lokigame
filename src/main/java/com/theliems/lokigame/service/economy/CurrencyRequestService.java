package com.theliems.lokigame.service.economy;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.entity.economy.CurrencyRequest;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.repository.economy.CurrencyRequestRepository;
import com.theliems.lokigame.repository.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyRequestService {

    private final CurrencyRequestRepository currencyRequestRepository;
    private final PlayerRepository playerRepository;
    private final ExceptionFactory exceptionFactory;

    @Transactional
    public CurrencyRequest createRequest(UUID playerId, Long amount, String reason) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Player", playerId));

        CurrencyRequest request = CurrencyRequest.builder()
                .player(player)
                .amount(amount)
                .reason(reason)
                .status(CurrencyRequest.RequestStatus.PENDING)
                .build();

        request = currencyRequestRepository.save(request);
        log.info("Currency request created: Player={}, Amount={}", playerId, amount);
        return request;
    }

    public List<CurrencyRequest> getPlayerRequests(UUID playerId) {
        return currencyRequestRepository.findByPlayer_PlayerId(playerId);
    }

    public List<CurrencyRequest> getPendingRequests() {
        return currencyRequestRepository.findByStatus(CurrencyRequest.RequestStatus.PENDING);
    }

    @Transactional
    public CurrencyRequest approveRequest(UUID requestId, UUID adminId, String notes) {
        CurrencyRequest request = currencyRequestRepository.findById(requestId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("CurrencyRequest", requestId));

        if (request.getStatus() != CurrencyRequest.RequestStatus.PENDING) {
            throw exceptionFactory.validationError("Request is not pending");
        }

        Player admin = playerRepository.findById(adminId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Admin", adminId));

        Player player = request.getPlayer();
        player.setCurrency(player.getCurrency() + request.getAmount());

        request.setStatus(CurrencyRequest.RequestStatus.APPROVED);
        request.setReviewedBy(admin);
        request.setAdminNotes(notes);

        playerRepository.save(player);
        request = currencyRequestRepository.save(request);

        log.info("Currency request approved: RequestId={}, Player={}, Amount={}, Admin={}",
                requestId, player.getPlayerId(), request.getAmount(), adminId);
        return request;
    }

    @Transactional
    public CurrencyRequest rejectRequest(UUID requestId, UUID adminId, String notes) {
        CurrencyRequest request = currencyRequestRepository.findById(requestId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("CurrencyRequest", requestId));

        if (request.getStatus() != CurrencyRequest.RequestStatus.PENDING) {
            throw exceptionFactory.validationError("Request is not pending");
        }

        Player admin = playerRepository.findById(adminId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Admin", adminId));

        request.setStatus(CurrencyRequest.RequestStatus.REJECTED);
        request.setReviewedBy(admin);
        request.setAdminNotes(notes);

        request = currencyRequestRepository.save(request);

        log.info("Currency request rejected: RequestId={}, Admin={}", requestId, adminId);
        return request;
    }
}
