package com.theliems.lokigame.repository.economy;

import com.theliems.lokigame.model.entity.economy.CurrencyRequest;
import com.theliems.lokigame.model.entity.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CurrencyRequestRepository extends JpaRepository<CurrencyRequest, UUID> {
    List<CurrencyRequest> findByPlayer(Player player);
    List<CurrencyRequest> findByPlayer_PlayerId(UUID playerId);
    List<CurrencyRequest> findByStatus(CurrencyRequest.RequestStatus status);
}
