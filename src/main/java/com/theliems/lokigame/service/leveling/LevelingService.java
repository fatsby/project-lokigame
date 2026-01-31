package com.theliems.lokigame.service.leveling;

import com.theliems.lokigame.constant.LevelingConstants;
import com.theliems.lokigame.model.dto.leveling.LevelUpResult;
import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.repository.hero.HeroRepository;
import com.theliems.lokigame.service.hero.HeroService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing hero leveling and XP.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LevelingService {

    private final XpCalculatorService xpCalculator;
    private final StatGrowthService statGrowthService;
    private final HeroRepository heroRepository;
    private final HeroService heroService;

    /**
     * Add XP to a hero and process any level-ups.
     *
     * @param hero     The hero to add XP to
     * @param xpAmount Amount of XP to add
     * @return Result containing level-up information
     */
    @Transactional
    public LevelUpResult addExperience(Hero hero, long xpAmount) {
        int maxLevel = LevelingConstants.getMaxLevel(hero.getStar());
        boolean atCap = hero.getLevel() >= maxLevel;

        // No XP gain if already at cap or no XP to add
        if (xpAmount <= 0 || atCap) {
            return LevelUpResult.noChange(
                    hero.getHeroId(),
                    hero.getFirstName() + " " + hero.getLastName(),
                    hero.getLevel(),
                    hero.getExperience(),
                    xpCalculator.calculateXpForNextLevel(hero),
                    atCap);
        }

        int startLevel = hero.getLevel();
        hero.setExperience(hero.getExperience() + xpAmount);

        int levelsGained = 0;
        while (canLevelUp(hero)) {
            levelUp(hero);
            levelsGained++;
        }

        // Check if now at cap after leveling
        atCap = hero.getLevel() >= maxLevel;

        // Recalculate final stats after level-up
        if (levelsGained > 0) {
            heroService.recalculateHeroStats(hero);
        }

        heroRepository.save(hero);

        log.info("Hero {} gained {} XP, leveled from {} to {} (gained {} levels)",
                hero.getHeroId(), xpAmount, startLevel, hero.getLevel(), levelsGained);

        return LevelUpResult.builder()
                .heroId(hero.getHeroId())
                .heroName(hero.getFirstName() + " " + hero.getLastName())
                .previousLevel(startLevel)
                .newLevel(hero.getLevel())
                .levelsGained(levelsGained)
                .currentXp(hero.getExperience())
                .xpToNextLevel(xpCalculator.calculateXpForNextLevel(hero))
                .xpGained(xpAmount)
                .atLevelCap(atCap)
                .build();
    }

    /**
     * Check if a hero can level up (has enough XP and not at rarity cap).
     */
    public boolean canLevelUp(Hero hero) {
        int maxLevel = LevelingConstants.getMaxLevel(hero.getStar());
        if (hero.getLevel() >= maxLevel) {
            return false;
        }

        long xpRequired = xpCalculator.calculateXpForNextLevel(hero);
        return hero.getExperience() >= xpRequired;
    }

    /**
     * Level up a hero once (assumes canLevelUp has been checked).
     */
    private void levelUp(Hero hero) {
        long xpRequired = xpCalculator.calculateXpForNextLevel(hero);
        hero.setExperience(hero.getExperience() - xpRequired);
        hero.setLevel(hero.getLevel() + 1);
        statGrowthService.applyLevelUpStats(hero);

        log.debug("Hero {} leveled up to {}", hero.getHeroId(), hero.getLevel());
    }

    /**
     * Get the maximum level for a hero based on their rarity.
     */
    public int getMaxLevel(Hero hero) {
        return LevelingConstants.getMaxLevel(hero.getStar());
    }
}
