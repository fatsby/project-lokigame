package com.theliems.lokigame.controller.session;

import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.service.session.SessionTrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for session management endpoints.
 * Client should call heartbeat every 30 seconds to maintain online status.
 */
@RestController
@RequestMapping("/api/session")
@RequiredArgsConstructor
public class HeartbeatController {

    private final SessionTrackingService sessionTrackingService;

    /**
     * Heartbeat endpoint to keep session alive.
     * Client should call this every 30 seconds.
     */
    @PostMapping("/heartbeat")
    public ResponseEntity<Void> heartbeat(@AuthenticationPrincipal Player player) {
        sessionTrackingService.updateHeartbeat(player.getPlayerId());
        return ResponseEntity.ok().build();
    }

    /**
     * Explicit logout endpoint.
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Player player) {
        sessionTrackingService.recordLogout(player.getPlayerId());
        return ResponseEntity.ok().build();
    }
}
