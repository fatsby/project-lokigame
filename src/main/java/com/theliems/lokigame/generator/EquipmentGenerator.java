package com.theliems.lokigame.generator;

import com.theliems.lokigame.model.entity.equipment.EquipmentStat;
import com.theliems.lokigame.model.entity.inventory.EquipmentItem;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.enums.EquipmentType;
import com.theliems.lokigame.model.enums.Rarity;
import com.theliems.lokigame.model.enums.StatType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates procedurally unique equipment items with random stats.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class EquipmentGenerator {

    private static final Map<Rarity, Double> RARITY_WEIGHTS = Map.of(
            Rarity.COMMON, 50.0,
            Rarity.RARE, 30.0,
            Rarity.EPIC, 15.0,
            Rarity.LEGENDARY, 5.0);

    private static final List<StatType> ALL_STAT_TYPES = Arrays.asList(StatType.values());

    /**
     * Generates a procedurally unique equipment piece.
     *
     * @param owner         The player who will own this equipment
     * @param equipmentType The type of equipment to generate
     * @param playerLevel   Player level for scaling
     * @param dungeonLevel  Dungeon level for scaling
     * @return A fully generated EquipmentItem with random rarity, stats, and values
     */
    public EquipmentItem generateEquipment(Player owner, EquipmentType equipmentType,
            Integer playerLevel, Integer dungeonLevel) {
        return generateEquipment(owner, equipmentType, playerLevel, dungeonLevel, null);
    }

    /**
     * Generates an equipment piece with optional forced rarity.
     *
     * @param owner         The player who will own this equipment
     * @param equipmentType The type of equipment
     * @param playerLevel   Player level
     * @param dungeonLevel  Dungeon level
     * @param forcedRarity  If not null, forces this rarity. If null, rolls
     *                      randomly.
     * @return Generated EquipmentItem
     */
    public EquipmentItem generateEquipment(Player owner, EquipmentType equipmentType,
            Integer playerLevel, Integer dungeonLevel,
            Rarity forcedRarity) {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        // Determine rarity: use forced if provided, else roll
        Rarity rarity = forcedRarity != null ? forcedRarity : rollRarity(random);

        // Calculate effective level (average of player and dungeon level)
        int effectiveLevel = Math.max(1, (playerLevel + dungeonLevel) / 2);

        // Create EquipmentItem
        EquipmentItem equipmentItem = new EquipmentItem(owner, equipmentType, rarity, effectiveLevel);

        // Generate base stats (always present)
        List<EquipmentStat> baseStats = generateBaseStats(equipmentType, rarity, effectiveLevel, random);
        baseStats.forEach(equipmentItem::addBaseStat);

        // Generate random stats (varies by rarity)
        int numRandomStats = random.nextInt(
                rarity.getMinRandomStats(),
                rarity.getMaxRandomStats() + 1);
        List<EquipmentStat> randomStats = generateRandomStats(numRandomStats, rarity, effectiveLevel, random);
        randomStats.forEach(equipmentItem::addRandomStat);

        log.debug("Generated {} equipment: rarity={}, level={}, baseStats={}, randomStats={}",
                equipmentType, rarity, effectiveLevel, baseStats.size(), randomStats.size());

        return equipmentItem;
    }

    private Rarity rollRarity(ThreadLocalRandom random) {
        double totalWeight = RARITY_WEIGHTS.values().stream().mapToDouble(Double::doubleValue).sum();
        double roll = random.nextDouble() * totalWeight;

        double cumulative = 0.0;
        for (Map.Entry<Rarity, Double> entry : RARITY_WEIGHTS.entrySet()) {
            cumulative += entry.getValue();
            if (roll <= cumulative) {
                return entry.getKey();
            }
        }
        return Rarity.COMMON; // Fallback
    }

    private List<EquipmentStat> generateBaseStats(EquipmentType equipmentType, Rarity rarity, int level,
            ThreadLocalRandom random) {
        List<EquipmentStat> baseStats = new ArrayList<>();

        // Primary stat based on equipment type
        StatType primaryStat = getPrimaryStatForType(equipmentType);
        double primaryValue = calculateStatValue(primaryStat, rarity, level, random, true);
        baseStats.add(EquipmentStat.builder()
                .statType(primaryStat)
                .value(primaryValue)
                .isBaseStat(true)
                .build());

        // Secondary stat (always HP for survivability)
        if (primaryStat != StatType.HP) {
            double hpValue = calculateStatValue(StatType.HP, rarity, level, random, true);
            baseStats.add(EquipmentStat.builder()
                    .statType(StatType.HP)
                    .value(hpValue)
                    .isBaseStat(true)
                    .build());
        }

        return baseStats;
    }

    private List<EquipmentStat> generateRandomStats(int count, Rarity rarity, int level, ThreadLocalRandom random) {
        List<EquipmentStat> randomStats = new ArrayList<>();
        Set<StatType> usedStats = new HashSet<>();

        // Shuffle available stats
        List<StatType> availableStats = new ArrayList<>(ALL_STAT_TYPES);
        Collections.shuffle(availableStats, random);

        for (int i = 0; i < count && i < availableStats.size(); i++) {
            StatType statType = availableStats.get(i);
            if (usedStats.add(statType)) {
                double value = calculateStatValue(statType, rarity, level, random, false);
                randomStats.add(EquipmentStat.builder()
                        .statType(statType)
                        .value(value)
                        .isBaseStat(false)
                        .build());
            }
        }

        return randomStats;
    }

    private StatType getPrimaryStatForType(EquipmentType equipmentType) {
        return switch (equipmentType) {
            case WEAPON -> StatType.ATK;
            case HELMET, ARMOR, BOOTS -> StatType.DEF;
            case RING, NECKLACE -> StatType.CRIT_RATE;
        };
    }

    private double calculateStatValue(StatType statType, Rarity rarity, int level, ThreadLocalRandom random,
            boolean isBase) {
        // Base value per level
        double baseValuePerLevel = getBaseValuePerLevel(statType);

        // Level scaling
        double levelMultiplier = 1.0 + (level - 1) * 0.1;

        // Rarity multiplier
        double rarityMultiplier = getRarityMultiplier(rarity);

        // Random variance (80% to 120% for base, 70% to 130% for random)
        double varianceMin = isBase ? 0.8 : 0.7;
        double varianceMax = isBase ? 1.2 : 1.3;
        double variance = random.nextDouble(varianceMin, varianceMax);

        return baseValuePerLevel * levelMultiplier * rarityMultiplier * variance;
    }

    private double getBaseValuePerLevel(StatType statType) {
        return switch (statType) {
            case HP -> 50.0;
            case ATK -> 10.0;
            case DEF -> 5.0;
            case CRIT_RATE -> 0.02; // 2% per level
            case CRIT_DAMAGE -> 0.05; // 5% per level
            case SPEED -> 2.0;
        };
    }

    private double getRarityMultiplier(Rarity rarity) {
        return switch (rarity) {
            case COMMON -> 1.0;
            case RARE -> 1.5;
            case EPIC -> 2.5;
            case LEGENDARY -> 4.0;
            case GODSENT -> 5.0;
        };
    }
}
