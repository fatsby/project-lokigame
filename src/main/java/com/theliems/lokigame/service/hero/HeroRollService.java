package com.theliems.lokigame.service.hero;

import com.theliems.lokigame.generator.EquipmentGenerator;
import com.theliems.lokigame.generator.HeroFactory;
import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.entity.equipment.Equipment;
import com.theliems.lokigame.model.entity.hero.*;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.enums.EquipmentSlot;
import com.theliems.lokigame.model.enums.EquipmentType;
import com.theliems.lokigame.model.entity.inventory.InventoryItem; // Added
import com.theliems.lokigame.model.enums.ItemType; // Added
import com.theliems.lokigame.model.enums.Rarity;
import com.theliems.lokigame.repository.equipment.EquipmentRepository;
import com.theliems.lokigame.repository.inventory.InventoryItemRepository; // Added
import com.theliems.lokigame.repository.hero.HeroClassRepository;
import com.theliems.lokigame.repository.hero.HeroRepository;
import com.theliems.lokigame.repository.hero.OriginRepository;
import com.theliems.lokigame.repository.hero.WorldRepository;
import com.theliems.lokigame.repository.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class HeroRollService {

    private final HeroFactory heroFactory;
    private final HeroRepository heroRepository;
    private final HeroClassRepository heroClassRepository;
    private final OriginRepository originRepository;
    private final WorldRepository worldRepository;
    private final PlayerRepository playerRepository;
    private final ExceptionFactory exceptionFactory;
    private final EquipmentGenerator equipmentGenerator;
    private final EquipmentRepository equipmentRepository;
    private final InventoryItemRepository inventoryItemRepository; // Injected
    private final HeroService heroService; // Injected

    private static final Long HERO_ROLL_COST = 100L; // Cost to roll a hero

    @Transactional
    public Hero rollHero(Player player) {
        // Check if player has enough currency
        if (player.getCurrency() < HERO_ROLL_COST) {
            throw exceptionFactory.validationError("Insufficient currency. Required: %d, Available: %d", HERO_ROLL_COST,
                    player.getCurrency());
        }

        // Deduct currency
        player.setCurrency(player.getCurrency() - HERO_ROLL_COST);
        playerRepository.save(player);

        // Get all available templates
        List<HeroClass> heroClasses = heroClassRepository.findAll();
        List<Origin> origins = originRepository.findAll();
        List<World> worlds = worldRepository.findAll();

        if (heroClasses.isEmpty() || origins.isEmpty() || worlds.isEmpty()) {
            throw exceptionFactory.internalError("Missing game data: HeroClasses, Origins, or Worlds not initialized");
        }

        // Random selection
        ThreadLocalRandom random = ThreadLocalRandom.current();
        HeroClass heroClass = heroClasses.get(random.nextInt(heroClasses.size()));
        Origin origin = origins.get(random.nextInt(origins.size()));
        World world = rollWorld(worlds, random);
        // Generate unique random seed
        long randomSeed = random.nextLong();

        // Generate hero
        Hero hero = heroFactory.generateHero(heroClass, origin, world, randomSeed);
        hero.setOwner(player);

        // Save hero
        hero = heroRepository.save(hero);

        // Generate and equip a full set of equipment
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            EquipmentType type = getEquipmentTypeForSlot(slot);
            if (type != null) {
                // Generate the Equipment definition (rolled stats)
                Equipment equipment = equipmentGenerator.generateEquipment(type, 1, 1, Rarity.COMMON);
                equipment = equipmentRepository.save(equipment); // Save the equipment definition

                // Create an InventoryItem instance for the player
                InventoryItem inventoryItem = InventoryItem.builder()
                        .owner(player)
                        .itemId(equipment.getId().toString()) // Use Equipment's ID as the item reference
                        .type(equipment.getEquipmentType().toItemType()) // Map EquipmentType to ItemType
                        .tier(equipment.getRarity().toItemTier()) // Map Rarity to ItemTier
                        .metadata(java.util.Map.of("equipmentId", equipment.getId().toString())) // Store reference to
                                                                                                 // actual Equipment
                                                                                                 // entity
                        .build();
                inventoryItem = inventoryItemRepository.save(inventoryItem);

                hero.getEquipment().put(slot, inventoryItem.getId()); // Store inventoryItemId
            }
        }
        hero = heroRepository.save(hero);

        // Recalculate stats with the new equipment
        heroService.recalculateHeroStats(hero);

        log.info("Player {} rolled hero: {} {} (Class: {}, Origin: {}, Star: {}). Cost: {}",
                player.getPlayerId(), hero.getFirstName(), hero.getLastName(),
                heroClass.getName(), origin.getName(), hero.getStar(), HERO_ROLL_COST);

        return hero;
    }

    private EquipmentType getEquipmentTypeForSlot(EquipmentSlot slot) {
        return switch (slot) {
            case WEAPON -> EquipmentType.WEAPON;
            case HELMET -> EquipmentType.HELMET;
            case ARMOR -> EquipmentType.ARMOR;
            case BOOTS -> EquipmentType.BOOTS;
            case RING -> EquipmentType.RING;
            case NECKLACE -> EquipmentType.NECKLACE;
            default -> null;
        };
    }

    private World rollWorld(List<World> worlds, Random random) {
        double totalWeight = worlds.stream()
                .mapToDouble(World::getRarityWeight)
                .sum();

        double roll = random.nextDouble() * totalWeight;

        double current = 0.0;
        for (World world : worlds) {
            current += world.getRarityWeight();
            if (roll <= current) {
                return world;
            }
        }

        // fallback (should not happen)
        return worlds.get(0);
    }
}
