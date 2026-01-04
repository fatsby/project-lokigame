package com.theliems.lokigame.service.hero;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.infrastructure.exception.errorCategories.GameDataError;
import com.theliems.lokigame.infrastructure.rng.WeightedSelector;
import com.theliems.lokigame.model.entity.hero.ClassDefinition;
import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.model.entity.hero.StatRange;
import com.theliems.lokigame.model.entity.inventory.InventoryItem;
import com.theliems.lokigame.model.entity.inventory.ItemDefinition;
import com.theliems.lokigame.model.entity.world.WorldDefinition;
import com.theliems.lokigame.model.enums.EquipmentSlot;
import com.theliems.lokigame.model.enums.HeroGender;
import com.theliems.lokigame.repository.hero.HeroRepository;
import com.theliems.lokigame.service.gameData.registry.HeroClassRegistry;
import com.theliems.lokigame.service.gameData.registry.ItemRegistry;
import com.theliems.lokigame.service.gameData.registry.NamesRegistry;
import com.theliems.lokigame.service.gameData.registry.VisualsRegistry;
import com.theliems.lokigame.service.gameData.registry.WorldRegistry;
import com.theliems.lokigame.service.inventory.InventoryItemService;
import com.theliems.lokigame.service.rng.WeightedRngService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HeroService {

    HeroRepository heroRepository;
    HeroClassRegistry heroClassRegistry;
    WorldRegistry worldRegistry;
    VisualsRegistry visualsRegistry;
    NamesRegistry namesRegistry;
    ItemRegistry itemRegistry;
    InventoryItemService inventoryItemService;
    WeightedRngService rngService;
    ExceptionFactory exceptionFactory;

    // Rarity Weights (Total ~100)
    private static final Map<Integer, Double> RARITY_WEIGHTS = Map.of(
            1, 70.0,
            2, 15.0,
            3, 7.0,
            4, 4.0,
            5, 2.0,
            6, 1.5,
            7, 0.5);

    // Stat Multipliers by Rarity
    private static final Map<Integer, Double> RARITY_STAT_MULTIPLIERS = Map.of(
            1, 1.0,
            2, 1.1,
            3, 1.25,
            4, 1.45,
            5, 1.8,
            6, 2.5,
            7, 4.0);

    @Transactional
    public Hero summonHero(UUID ownerId) {
        // 1. Roll Rarity
        int rarity = rollRarity();

        // 2. Roll Class
        ClassDefinition heroClass = heroClassRegistry.getRandomClass();
        if (heroClass == null) {
            throw exceptionFactory.createCustomException(GameDataError.HERO_CLASS_REGISTRY_EMPTY);
        }

        // 3. Roll World
        WorldDefinition world = worldRegistry.rollRandomWorld();

        // 4. Roll Gender (50/50)
        HeroGender gender = ThreadLocalRandom.current().nextBoolean() ? HeroGender.MALE : HeroGender.FEMALE;

        // 5. Generate Name (based on gender)
        String firstName = namesRegistry.getRandomFirstName(gender);
        String lastName = namesRegistry.getRandomLastName();

        // 6. Calculate Stats
        Map<String, Double> stats = calculateStats(heroClass, world, rarity);

        // 7. Generate Visuals
        Map<String, String> visuals = generateVisuals();

        // 8. Calculate Growth Attributes
        double willPower = ThreadLocalRandom.current().nextDouble(1.0, 2.0); // Random willPower for each hero
        double expPerSec = 0.001; // base value, each hero shares the same expPerSec

        // 9. Create Default Equipment
        Map<EquipmentSlot, UUID> equipment = createDefaultEquipment(ownerId, heroClass.getId());

        // 10. Build Hero
        Hero hero = Hero.builder()
                .ownerId(ownerId)
                .heroClass(heroClass.getId())
                .gender(gender)
                .firstName(firstName)
                .lastName(lastName)
                .rarity(rarity)
                .originWorldId(world.getId())
                .level(1)
                .experience(0L)
                .willPower(willPower)
                .expPerSecond(expPerSec)
                .stats(stats)
                .visuals(visuals)
                .equipment(equipment)
                .build();

        return heroRepository.save(hero);
    }

    /**
     * Creates default inventory items for each equipment slot and returns a map of
     * slot -> item UUID.
     */
    private Map<EquipmentSlot, UUID> createDefaultEquipment(UUID ownerId, String classId) {
        Map<EquipmentSlot, UUID> equipment = new HashMap<>();

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemDefinition defaultItemDef = itemRegistry.getBaseItem(slot, classId);
            if (defaultItemDef != null) {
                InventoryItem createdItem = inventoryItemService.createDefaultItem(
                        ownerId,
                        defaultItemDef.getId(),
                        defaultItemDef.getType());
                equipment.put(slot, createdItem.getId());
            }
        }

        return equipment;
    }

    private int rollRarity() {
        WeightedSelector<Integer> selector = rngService.createSelector();
        RARITY_WEIGHTS.forEach((rarity, weight) -> selector.add(weight, rarity));
        return selector.next();
    }

    private Map<String, Double> calculateStats(ClassDefinition heroClass, WorldDefinition world, int rarity) {
        Map<String, Double> finalStats = new HashMap<>();
        double worldMult = world.getStatMultiplier();
        double rarityMult = RARITY_STAT_MULTIPLIERS.getOrDefault(rarity, 1.0);

        if (heroClass.getBaseStats() != null) {
            for (Map.Entry<String, StatRange> entry : heroClass.getBaseStats().entrySet()) {
                String statName = entry.getKey();
                StatRange range = entry.getValue();

                double baseRoll = ThreadLocalRandom.current().nextDouble(range.getMin(), range.getMax());
                double finalVal = baseRoll * worldMult * rarityMult;

                // Round to 2 decimal places
                finalVal = Math.round(finalVal * 100.0) / 100.0;
                finalStats.put(statName, finalVal);
            }
        }
        return finalStats;
    }

    private Map<String, String> generateVisuals() {
        Map<String, String> visuals = new HashMap<>();
        visuals.put("hair_id", visualsRegistry.getRandomHair());
        visuals.put("face_id", visualsRegistry.getRandomFace());
        // Top/Bottom could be added here if registry supported them
        return visuals;
    }
}
