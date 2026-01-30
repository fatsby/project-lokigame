package com.theliems.lokigame.service.session;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Service for tracking player session status using Redis.
 * Uses heartbeat mechanism to detect implicit logouts.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SessionTrackingService {

    private final StringRedisTemplate redisTemplate;

    private static final String SESSION_PREFIX = "session:";

    /**
     * Timeout in seconds after which a player without heartbeat is considered
     * offline.
     * Client should send heartbeat every 30 seconds.
     */
    private static final long HEARTBEAT_TIMEOUT_SECONDS = 60;

    /**
     * Record login time. Called when player authenticates.
     */
    public void recordLogin(UUID playerId) {
        String key = SESSION_PREFIX + playerId;
        String now = Instant.now().toString();

        redisTemplate.opsForHash().put(key, "lastLoginTime", now);
        redisTemplate.opsForHash().put(key, "lastHeartbeat", now);
        redisTemplate.opsForHash().put(key, "isOnline", "true");

        log.debug("Recorded login for player {}", playerId);
    }

    /**
     * Update heartbeat timestamp. Called periodically by client.
     */
    public void updateHeartbeat(UUID playerId) {
        String key = SESSION_PREFIX + playerId;
        redisTemplate.opsForHash().put(key, "lastHeartbeat", Instant.now().toString());
        redisTemplate.opsForHash().put(key, "isOnline", "true");
    }

    /**
     * Explicit logout. Called when player explicitly logs out.
     */
    public void recordLogout(UUID playerId) {
        String key = SESSION_PREFIX + playerId;
        redisTemplate.opsForHash().put(key, "lastLogoutTime", Instant.now().toString());
        redisTemplate.opsForHash().put(key, "isOnline", "false");

        log.debug("Recorded logout for player {}", playerId);
    }

    /**
     * Calculate offline duration since last activity.
     * If player has no session data (#first login), returns Duration.ZERO.
     * If player is still considered online (within heartbeat timeout), returns
     * Duration.ZERO.
     */
    public Duration getOfflineDuration(UUID playerId) {
        String key = SESSION_PREFIX + playerId;
        String lastHeartbeat = (String) redisTemplate.opsForHash().get(key, "lastHeartbeat");

        if (lastHeartbeat == null) {
            // First login ever - no offline XP
            return Duration.ZERO;
        }

        Instant lastActive = Instant.parse(lastHeartbeat);
        Instant effectiveLogout = lastActive.plusSeconds(HEARTBEAT_TIMEOUT_SECONDS);
        Instant now = Instant.now();

        if (now.isBefore(effectiveLogout)) {
            // Still within heartbeat window - considered online
            return Duration.ZERO;
        }

        // Calculate duration from effective logout to now
        return Duration.between(effectiveLogout, now);
    }

    /**
     * Check if player is currently online (within heartbeat timeout).
     */
    public boolean isOnline(UUID playerId) {
        String key = SESSION_PREFIX + playerId;
        String lastHeartbeat = (String) redisTemplate.opsForHash().get(key, "lastHeartbeat");

        if (lastHeartbeat == null) {
            return false;
        }

        Instant lastActive = Instant.parse(lastHeartbeat);
        Instant timeoutThreshold = Instant.now().minusSeconds(HEARTBEAT_TIMEOUT_SECONDS);

        return lastActive.isAfter(timeoutThreshold);
    }
}
