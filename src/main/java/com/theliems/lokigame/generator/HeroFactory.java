package com.theliems.lokigame.generator;

import com.theliems.lokigame.model.entity.hero.*;
import com.theliems.lokigame.model.enums.HeroGender;
import com.theliems.lokigame.model.enums.StatType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class HeroFactory {

    /**
     * Generates a procedurally unique hero.
     * Each hero is guaranteed to be unique through UUID + random seed combination.
     *
     * @param heroClass  The hero class template
     * @param origin     The origin template
     * @param originWorld The world template
     * @param randomSeed Random seed for uniqueness
     * @return A fully generated Hero with unique stats and personality
     */
    public Hero generateHero(HeroClass heroClass, Origin origin, World originWorld, Long randomSeed) {
        java.util.Random random = new java.util.Random(randomSeed);

        // Generate unique name
        String firstName = generateFirstName(random);
        String lastName = generateLastName(random);

        // Generate gender
        HeroGender gender = random.nextBoolean() ? HeroGender.MALE : HeroGender.FEMALE;

        // Roll rarity/star (1-7 stars)
        int star = rollStar(random);

        // Generate base stats from class
        List<HeroStats> stats = generateHeroStats(heroClass, origin, star, random,originWorld);

        // Generate unique personality seed
        long personalitySeed = random.nextLong();

        Hero hero = Hero.builder()
                .heroClass(heroClass)
                .origin(origin)
                .originWorld(originWorld)
                .firstName(firstName)
                .lastName(lastName)
                .gender(gender)
                .star(star)
                .level(1)
                .experience(0L)
                .randomSeed(randomSeed)
                .willPower(0.8 + random.nextDouble() * 0.4) // 0.8 to 1.2
                .expPerSecond(0.001)
                .equipment(new HashMap<>())
                .stats(stats)
                .build();

        // Set bidirectional relationships
        stats.forEach(stat -> stat.setHero(hero));

        log.debug("Generated hero: {} {} (Class: {}, Origin: {}, Star: {})",
                firstName, lastName, heroClass.getName(), origin.getName(), star);

        return hero;
    }

    private int rollStar(java.util.Random random) {
        // Weighted star distribution (1-7 stars)
        // Lower stars are more common
        double roll = random.nextDouble();
        if (roll < 0.50) return 1; // 50% chance
        if (roll < 0.75) return 2; // 25% chance
        if (roll < 0.90) return 3; // 15% chance
        if (roll < 0.97) return 4; // 7% chance
        if (roll < 0.99) return 5; // 2% chance
        if (roll < 0.999) return 6; // 0.9% chance
        return 7; // 0.1% chance
    }

    private List<HeroStats> generateHeroStats(HeroClass heroClass, Origin origin, int star, java.util.Random random,World world) {
        List<HeroStats> stats = new ArrayList<>();

        // Star multiplier (higher star = better stats)
        double starMultiplier = 1.0 + (star - 1) * 0.2; // 1.0, 1.2, 1.4, 1.6, 1.8, 2.0, 2.2

        for (StatType statType : StatType.values()) {
            // Get base value from class
            double baseValue = heroClass.getBaseStats().getOrDefault(statType, getDefaultBaseValue(statType));

            // Apply class modifiers
            double classModifier = heroClass.getStatModifiers().getOrDefault(statType, 0.0);
            baseValue *= (1.0 + classModifier);

            // Apply origin modifiers
            double originModifier = origin.getStatModifiers().getOrDefault(statType, 0.0);
            baseValue *= (1.0 + originModifier);

            // Apply star multiplier
            baseValue *= starMultiplier;

            // need to optimize later
            double worldModifier = world.getStatMultiplier();
            baseValue *= (1.0 + worldModifier);

            // Add random variance (90% to 110%)
            double variance = 0.9 + random.nextDouble() * 0.2; // 0.9 to 1.1
            baseValue *= variance;


            HeroStats heroStat = HeroStats.builder()
                    .statType(statType)
                    .baseValue(baseValue)
                    .finalValue(baseValue) // Will be recalculated with equipment
                    .build();

            stats.add(heroStat);
        }

        return stats;
    }

    private double getDefaultBaseValue(StatType statType) {
        return switch (statType) {
            case HP -> 100.0;
            case ATK -> 20.0;
            case DEF -> 10.0;
            case CRIT_RATE -> 0.05; // 5%
            case CRIT_DAMAGE -> 1.5; // 150%
            case SPEED -> 50.0;
        };
    }

    private String generateFirstName(java.util.Random random) {
        // Placeholder - in production, use a name generator or database
        String[] firstNames = {"Aria", "Kael", "Luna", "Thorin", "Zara", "Drake", "Nova", "Rex", "Ivy", "Orion"};
        return firstNames[random.nextInt(firstNames.length)];
    }

    private String generateLastName(java.util.Random random) {
        // Placeholder - in production, use a name generator or database
        String[] lastNames = {"Storm", "Shadow", "Flame", "Frost", "Light", "Dark", "Star", "Moon", "Sun", "Void"};
        return lastNames[random.nextInt(lastNames.length)];
    }





}
