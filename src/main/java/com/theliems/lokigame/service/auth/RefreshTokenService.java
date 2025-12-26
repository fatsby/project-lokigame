package com.theliems.lokigame.service.auth;

import com.theliems.lokigame.infrastructure.security.JwtProperties;
import com.theliems.lokigame.model.entity.system.RefreshToken;
import com.theliems.lokigame.repository.PlayerRepository;
import com.theliems.lokigame.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final PlayerRepository playerRepository;
    private final JwtProperties jwtProperties;

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken createRefreshToken(UUID userId) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setPlayer(playerRepository.findById(userId).get());
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtProperties.getRefreshExpiration()));
        refreshToken.setToken(UUID.randomUUID().toString());

        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token was expired. Please make a new signin request");
        }

        return token;
    }

    @Transactional
    public int deleteByUserId(UUID userId) {
        return refreshTokenRepository.deleteByPlayer(playerRepository.findById(userId).get());
    }
}
