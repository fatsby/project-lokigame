package com.theliems.lokigame.service.auth;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;

import com.theliems.lokigame.infrastructure.security.JwtTokenProvider;
import com.theliems.lokigame.model.dto.auth.AuthResponse;
import com.theliems.lokigame.model.dto.auth.LoginRequest;
import com.theliems.lokigame.model.dto.auth.RegisterRequest;
import com.theliems.lokigame.model.dto.auth.TokenPair;
import com.theliems.lokigame.model.dto.leveling.OfflineProgressionResult;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.entity.player.Role;
import com.theliems.lokigame.repository.player.PlayerRepository;
import com.theliems.lokigame.service.leveling.OfflineProgressionService;
import com.theliems.lokigame.service.session.SessionTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

        private final PlayerRepository playerRepository;
        private final PasswordEncoder passwordEncoder;
        private final JwtTokenProvider tokenProvider;
        private final AuthenticationManager authenticationManager;
        private final ExceptionFactory exceptionFactory;
        private final SessionTrackingService sessionTrackingService;
        private final OfflineProgressionService offlineProgressionService;

        @Transactional
        public AuthResponse register(RegisterRequest request) {
                if (playerRepository.existsByUsername(request.getUsername())) {
                        throw exceptionFactory.validationError("Username already exists");
                }
                if (playerRepository.existsByEmail(request.getEmail())) {
                        throw exceptionFactory.validationError("Email already exists");
                }

                Player player = Player.builder()
                                .username(request.getUsername())
                                .email(request.getEmail())
                                .passwordHash(passwordEncoder.encode(request.getPassword()))
                                .role(Role.ROLE_USER)
                                .currency(1000L) // Starting premium currency
                                .gold(0L) // Starting gold
                                .build();

                player = playerRepository.save(player);

                // Record initial login session
                sessionTrackingService.recordLogin(player.getPlayerId());

                String accessToken = tokenProvider.generateToken(player.getPlayerId(), player.getUsername(),
                                player.getRole().name());
                String refreshToken = tokenProvider.generateRefreshToken(player.getPlayerId(), player.getUsername());

                log.info("User registered: {}", player.getUsername());

                return AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .tokenType("Bearer")
                                .build();
        }

        @Transactional
        public AuthResponse login(LoginRequest request) {
                Authentication authentication = authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

                Player player = playerRepository.findByUsername(request.getUsername())
                                .orElseThrow(() -> exceptionFactory.resourceNotFound("User", request.getUsername()));

                // Process offline progression before recording new login
                OfflineProgressionResult progressionResult = offlineProgressionService
                                .processOfflineProgression(player);
                if (progressionResult.getTotalXpGained() > 0) {
                        log.info("Player {} earned {} offline XP across {} heroes",
                                        player.getUsername(), progressionResult.getTotalXpGained(),
                                        progressionResult.getHeroResults().size());
                }

                // Record login session
                sessionTrackingService.recordLogin(player.getPlayerId());

                String accessToken = tokenProvider.generateToken(player.getPlayerId(), player.getUsername(),
                                player.getRole().name());
                String refreshToken = tokenProvider.generateRefreshToken(player.getPlayerId(), player.getUsername());

                log.info("User logged in: {}", player.getUsername());

                return AuthResponse.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .tokenType("Bearer")
                                .offlineProgression(progressionResult)
                                .build();
        }

        public TokenPair refreshToken(String refreshToken) {
                if (!tokenProvider.validateToken(refreshToken)) {
                        throw exceptionFactory.unauthorized("Invalid refresh token");
                }

                UUID userId = tokenProvider.getUserIdFromToken(refreshToken);
                Player player = playerRepository.findById(userId)
                                .orElseThrow(() -> exceptionFactory.resourceNotFound("User", userId));

                String newAccessToken = tokenProvider.generateToken(player.getPlayerId(), player.getUsername(),
                                player.getRole().name());
                String newRefreshToken = tokenProvider.generateRefreshToken(player.getPlayerId(), player.getUsername());

                return TokenPair.builder()
                                .accessToken(newAccessToken)
                                .refreshToken(newRefreshToken)
                                .build();
        }
}
