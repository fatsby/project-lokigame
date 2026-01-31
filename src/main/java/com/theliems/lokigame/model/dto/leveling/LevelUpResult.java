package com.theliems.lokigame.model.dto.leveling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO representing the result of adding XP to a hero.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LevelUpResult {

    /**
     * Hero ID
     */
    private UUID heroId;

    /**
     * Hero name for display
     */
    private String heroName;

    /**
     * Level before XP was added
     */
    private int previousLevel;

    /**
     * Level after XP was added
     */
    private int newLevel;

    /**
     * Number of levels gained
     */
    private int levelsGained;

    /**
     * Current XP after addition
     */
    private long currentXp;

    /**
     * XP required for the next level
     */
    private long xpToNextLevel;

    /**
     * XP that was added
     */
    private long xpGained;

    /**
     * Whether the hero reached their rarity-based level cap
     */
    private boolean atLevelCap;

    /**
     * Create a result indicating no change occurred.
     */
    public static LevelUpResult noChange(UUID heroId, String heroName, int level, long currentXp, long xpToNextLevel,
            boolean atCap) {
        return LevelUpResult.builder()
                .heroId(heroId)
                .heroName(heroName)
                .previousLevel(level)
                .newLevel(level)
                .levelsGained(0)
                .currentXp(currentXp)
                .xpToNextLevel(xpToNextLevel)
                .xpGained(0)
                .atLevelCap(atCap)
                .build();
    }
}
