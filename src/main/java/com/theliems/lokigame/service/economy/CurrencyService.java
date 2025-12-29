package com.theliems.lokigame.service.economy;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.infrastructure.exception.errorCategories.EconomyError;
import com.theliems.lokigame.infrastructure.exception.errorCategories.PlayerError;
import com.theliems.lokigame.model.entity.economy.CurrencyTransaction;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.enums.TransactionSource;
import com.theliems.lokigame.model.enums.TransactionType;
import com.theliems.lokigame.repository.economy.CurrencyTransactionRepository;
import com.theliems.lokigame.repository.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    private final PlayerRepository playerRepository;
    private final CurrencyTransactionRepository transactionRepository;
    private final ExceptionFactory exceptionFactory;

    /**
     * Checks if a player has enough currency without locking.
     * Useful for UI pre-checks.
     */
    @Transactional(readOnly = true)
    public boolean hasEnough(UUID playerId, long amount) {
        if (amount < 0) {
            throw exceptionFactory.createCustomException(EconomyError.NEGATIVE_AMOUNT);
        }
        return playerRepository.findById(playerId)
                .map(p -> p.getCurrency() >= amount)
                .orElseThrow(() -> exceptionFactory.createNotFoundException("Player", playerId, PlayerError.PLAYER_NOT_FOUND));
    }

    /**
     * Deducts currency from a player's balance.
     * Uses pessimistic locking to prevent race conditions.
     */
    @Transactional
    public void withdraw(UUID playerId, long amount, TransactionSource source) {
        if (amount < 0) {
            throw exceptionFactory.createCustomException(EconomyError.NEGATIVE_AMOUNT);
        }

        Player player = playerRepository.findByIdWithLock(playerId)
                .orElseThrow(() -> exceptionFactory.createNotFoundException("Player", playerId, PlayerError.PLAYER_NOT_FOUND));

        if (player.getCurrency() < amount) {
            log.warn("Player {} attempted to withdraw {} but has only {}", playerId, amount, player.getCurrency());
            throw exceptionFactory.createCustomException(EconomyError.INSUFFICIENT_FUNDS);
        }

        long previousBalance = player.getCurrency();
        player.setCurrency(previousBalance - amount);
        playerRepository.save(player);

        recordTransaction(player, amount, player.getCurrency(), TransactionType.WITHDRAW, source);
        log.info("Player {} withdrew {}. New Balance: {}. Source: {}", playerId, amount, player.getCurrency(), source);
    }

    /**
     * Adds currency to a player's balance.
     * Uses pessimistic locking to prevent race conditions.
     */
    @Transactional
    public void deposit(UUID playerId, long amount, TransactionSource source) {
        if (amount < 0) {
            throw exceptionFactory.createCustomException(EconomyError.NEGATIVE_AMOUNT);
        }

        Player player = playerRepository.findByIdWithLock(playerId)
                .orElseThrow(() -> exceptionFactory.createNotFoundException("Player", playerId, PlayerError.PLAYER_NOT_FOUND));

        long previousBalance = player.getCurrency();
        player.setCurrency(previousBalance + amount);
        playerRepository.save(player);

        recordTransaction(player, amount, player.getCurrency(), TransactionType.DEPOSIT, source);
        log.info("Player {} deposited {}. New Balance: {}. Source: {}", playerId, amount, player.getCurrency(), source);
    }

    private void recordTransaction(Player player, long amount, long balanceAfter, TransactionType type, TransactionSource source) {
        CurrencyTransaction transaction = CurrencyTransaction.builder()
                .player(player)
                .amount(amount)
                .balanceAfter(balanceAfter)
                .type(type)
                .source(source)
                .timestamp(LocalDateTime.now())
                .build();
        transactionRepository.save(transaction);
    }
}
