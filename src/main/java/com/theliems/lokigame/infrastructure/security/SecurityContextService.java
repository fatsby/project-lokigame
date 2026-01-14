package com.theliems.lokigame.infrastructure.security;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.infrastructure.exception.errorCategories.PlayerError;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.repository.player.PlayerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Centralized service for accessing the current security context.
 * Provides methods to retrieve the authenticated user's information.
 */
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityContextService {

    PlayerRepository playerRepository;
    ExceptionFactory exceptionFactory;

    /**
     * Get the currently authenticated player entity.
     *
     * @return the Player entity for the authenticated user
     * @throws NotFoundException if the player is not found in the database
     */
    public Player getCurrentPlayer() {
        String username = getCurrentUsername();
        return playerRepository.findByUsername(username)
                .orElseThrow(() -> exceptionFactory.createNotFoundException(
                        "Player", "username", username, PlayerError.PLAYER_NOT_FOUND));
    }

    /**
     * Get the username of the currently authenticated user.
     *
     * @return the username from the security context
     */
    public String getCurrentUsername() {
        return SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();
    }

    /**
     * Get the UUID of currently authenticated user
     * @return the UUID from the security context
     */
    public UUID getCurrentPlayerId() {
        return getCurrentPlayer().getId();
    }
}
