package com.theliems.lokigame.service.leveling;

import com.theliems.lokigame.constant.LevelingConstants;
import com.theliems.lokigame.model.entity.dungeon.DropTable;
import com.theliems.lokigame.model.entity.hero.Hero;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * Service for XP-related calculations.
 */
@Service
@Slf4j
public class XpCalculatorService {

    /**
     * Calculate XP required to reach the next level.
     * Formula: XP_BASE * level^XP_EXPONENT
     *
     * @param hero The hero to calculate required XP
     * @return XP required for next level
     */
    public long calculateXpForNextLevel(Hero hero) {
        int level = hero.getLevel();
        int star = hero.getStar();

        double multiplier = LevelingConstants.STAR_XP_MULTIPLIERS.getOrDefault(star, 1.0);

        double rawXp = LevelingConstants.XP_BASE * Math.pow(level, LevelingConstants.XP_EXPONENT);

        return (long) (rawXp * multiplier);
    }

//    /**
//     * Calculate total XP required from level 1 to target level.
//     *
//     * @param targetLevel Target level
//     * @return Total cumulative XP required
//     */
//    public long calculateTotalXpForLevel(int targetLevel) {
//        long totalXp = 0;
//        for (int level = 1; level < targetLevel; level++) {
//            totalXp += calculateXpForNextLevel(level);
//        }
//        return totalXp;
//    }

    /**
     * Calculate XP gained from offline time.
     *
     * @param hero            Hero to calculate for
     * @param offlineDuration Duration player was offline
     * @return XP gained from idle
     */
    public long calculateOfflineXp(Hero hero, Duration offlineDuration) {
        if (offlineDuration.isZero() || offlineDuration.isNegative()) {
            return 0L;
        }

        long seconds = offlineDuration.getSeconds();
        long offlineXpResult = (long) (hero.getExpPerSecond() * hero.getWillPower() * seconds);
        log.debug("Calculated {} offline xp for {} seconds for Hero {}", offlineXpResult, seconds, hero.getFirstName());
        return offlineXpResult;
    }

    /**
     * Calculate XP from dungeon battle.
     * Returns 0 if heroes lost.
     *
     * @param dropTable Dungeon drop table with XP configuration
     * @param victory   Whether heroes won the battle
     * @return XP reward (0 on loss)
     */
    public long calculateBattleXp(DropTable dropTable, boolean victory) {
        if (!victory) {
            return 0L;
        }
        return (long) (dropTable.getBaseXp() * dropTable.getXpMultiplier());
    }
}
