package com.theliems.lokigame.repository;

import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.entity.system.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    @Modifying
    int deleteByPlayer(Player player);
}
