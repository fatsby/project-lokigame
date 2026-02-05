package com.theliems.lokigame.service.dungeon;

import com.theliems.lokigame.infrastructure.constants.DungeonConstants;
import com.theliems.lokigame.model.entity.dungeon.Monster;
import com.theliems.lokigame.model.entity.dungeon.MonsterTemplate;
import com.theliems.lokigame.model.enums.StatType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service responsible for scaling MonsterTemplates to specific dungeon levels.
 * Single Responsibility: Monster stat calculation and scaling.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MonsterScalingService {

    /**
     * Scale a MonsterTemplate to a specific level with world difficulty modifier.
     * 
     * Formula: finalStat = (baseStat + (growthPerLevel * level)) *
     * (MONSTER_STAT_SCALING ^ level) * worldDifficultyMod
     * 
     * @param template           The monster template to scale
     * @param level              The dungeon level
     * @param worldDifficultyMod The world's difficulty modifier
     * @return A scaled Monster instance
     */
    public Monster scaleToLevel(MonsterTemplate template, int level, double worldDifficultyMod) {
        Map<StatType, Double> scaledStats = new HashMap<>();

        for (StatType statType : StatType.values()) {
            double baseStat = template.getBaseStats().getOrDefault(statType, 0.0);
            double growthPerLevel = template.getStatGrowthPerLevel().getOrDefault(statType, 0.0);

            // Linear growth + compound scaling + world modifier
            double linearGrowth = baseStat + (growthPerLevel * level);
            double compoundScaling = Math.pow(DungeonConstants.MONSTER_STAT_SCALING, level);
            double finalStat = linearGrowth * compoundScaling * worldDifficultyMod;

            scaledStats.put(statType, finalStat);
        }

        log.debug("Scaled monster {} to level {} with worldMod {}", template.getName(), level, worldDifficultyMod);

        return Monster.builder()
                .id(UUID.randomUUID())
                .name(template.getName())
                .description(template.getDescription())
                .templateId(template.getId())
                .level(level)
                .stats(scaledStats)
                .build();
    }

    /**
     * Scale multiple MonsterTemplates to a specific level.
     * 
     * @param templates          The monster templates to scale
     * @param level              The dungeon level
     * @param worldDifficultyMod The world's difficulty modifier
     * @return List of scaled Monster instances
     */
    public List<Monster> scaleAllToLevel(List<MonsterTemplate> templates, int level, double worldDifficultyMod) {
        return templates.stream()
                .map(template -> scaleToLevel(template, level, worldDifficultyMod))
                .collect(Collectors.toList());
    }
}
