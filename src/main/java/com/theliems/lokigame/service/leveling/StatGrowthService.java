package com.theliems.lokigame.service.leveling;

import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.model.entity.hero.HeroClass;
import com.theliems.lokigame.model.entity.hero.HeroStats;
import com.theliems.lokigame.model.enums.StatType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Service for applying stat increases when a hero levels up.
 * Stat growth is class-specific.
 */
@Service
@Slf4j
public class StatGrowthService {

    /**
     * Apply level-up stat increases based on hero's class.
     * Each stat is increased by the class-specific growth value.
     *
     * @param hero The hero that leveled up
     */
    public void applyLevelUpStats(Hero hero) {
        HeroClass heroClass = hero.getHeroClass();

        if (heroClass == null) {
            log.warn("Hero {} has no class assigned, skipping stat growth", hero.getHeroId());
            return;
        }

        Map<StatType, Double> growthMap = heroClass.getStatGrowthPerLevel();

        if (growthMap == null || growthMap.isEmpty()) {
            log.warn("HeroClass {} has no stat growth defined", heroClass.getName());
            return;
        }

        for (HeroStats stat : hero.getStats()) {
            Double growth = growthMap.get(stat.getStatType());
            if (growth != null && growth > 0) {
                double oldValue = stat.getBaseValue();
                stat.setBaseValue(oldValue + growth);
                log.debug("Hero {} stat {} increased from {} to {}",
                        hero.getHeroId(), stat.getStatType(), oldValue, stat.getBaseValue());
            }
        }
    }
}
