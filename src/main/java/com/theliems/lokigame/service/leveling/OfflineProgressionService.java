package com.theliems.lokigame.service.leveling;

import com.theliems.lokigame.model.dto.leveling.LevelUpResult;
import com.theliems.lokigame.model.dto.leveling.OfflineProgressionResult;
import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.repository.hero.HeroRepository;
import com.theliems.lokigame.service.session.SessionTrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for processing offline (idle) XP progression.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OfflineProgressionService {

    private final SessionTrackingService sessionTracker;
    private final XpCalculatorService xpCalculator;
    private final LevelingService levelingService;
    private final HeroRepository heroRepository;

    /**
     * Process offline XP for all player heroes.
     * Called on login.
     *
     * @param player The player who is logging in
     * @return Result containing XP gains for all heroes
     */
    @Transactional
    public OfflineProgressionResult processOfflineProgression(Player player) {
        Duration offlineDuration = sessionTracker.getOfflineDuration(player.getPlayerId());

        if (offlineDuration.isZero() || offlineDuration.isNegative()) {
            log.debug("Player {} has no offline duration, skipping progression", player.getPlayerId());
            return OfflineProgressionResult.empty();
        }

        List<LevelUpResult> results = new ArrayList<>();
        List<Hero> heroes = heroRepository.findByPlayerIdAndAlive(player.getPlayerId());

        long totalXpGained = 0;
        int totalLevelsGained = 0;

        for (Hero hero : heroes) {
            long xpGained = xpCalculator.calculateOfflineXp(hero, offlineDuration);
            if (xpGained > 0) {
                LevelUpResult result = levelingService.addExperience(hero, xpGained);
                results.add(result);
                totalXpGained += xpGained;
                totalLevelsGained += result.getLevelsGained();
            }
        }

        log.info(
                "Player {} processed offline progression: {} seconds offline, {} XP gained, {} levels gained across {} heroes",
                player.getPlayerId(), offlineDuration.getSeconds(), totalXpGained, totalLevelsGained, heroes.size());

        return OfflineProgressionResult.builder()
                .offlineDurationSeconds(offlineDuration.getSeconds())
                .heroResults(results)
                .totalXpGained(totalXpGained)
                .totalLevelsGained(totalLevelsGained)
                .build();
    }
}
