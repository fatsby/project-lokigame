package com.theliems.lokigame.service.hero;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.model.entity.hero.HeroStats;
import com.theliems.lokigame.model.entity.equipment.Equipment;
import com.theliems.lokigame.model.entity.inventory.InventoryItem; // Added
import com.theliems.lokigame.model.enums.EquipmentSlot;
import com.theliems.lokigame.model.enums.StatType;
import com.theliems.lokigame.repository.equipment.EquipmentRepository;
import com.theliems.lokigame.repository.hero.HeroRepository;
import com.theliems.lokigame.repository.inventory.InventoryItemRepository; // Added
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HeroService {

    private final HeroRepository heroRepository;
    private final EquipmentRepository equipmentRepository;
    private final InventoryItemRepository inventoryItemRepository; // Injected

    public List<Hero> getPlayerHeroes(UUID playerId) {
        List<Hero> heroes = heroRepository.findByPlayerIdFull(playerId);
        // Recalculate final stats with equipment
        heroes.forEach(this::recalculateHeroStats);
        return heroes;
    }

    private final ExceptionFactory exceptionFactory;

    public Hero getHeroById(UUID heroId) {
        Hero hero = heroRepository.findByIdWithDetails(heroId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Hero", heroId));
        recalculateHeroStats(hero);
        return hero;
    }

    public void deleteHero(UUID heroId) {
        if (!heroRepository.existsById(heroId)) {
            throw exceptionFactory.resourceNotFound("Hero", heroId);
        }
        heroRepository.deleteById(heroId);
    }

    @Transactional
    public Hero equipItem(UUID heroId, EquipmentSlot slot, UUID inventoryItemId) { // Changed equipmentId to
                                                                                   // inventoryItemId
        Hero hero = heroRepository.findById(heroId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Hero", heroId));

        InventoryItem inventoryItem = inventoryItemRepository.findById(inventoryItemId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("InventoryItem", inventoryItemId));

        // Validate item type
        if (!inventoryItem.getType().isEquipment()) { // Assuming isEquipment() method on ItemType
            throw exceptionFactory.validationError("Inventory item " + inventoryItemId + " is not an equippable item.");
        }

        // Validate ownership: Check if inventoryItem owner matches hero owner
        if (!inventoryItem.getOwner().getPlayerId().equals(hero.getOwner().getPlayerId())) {
            throw exceptionFactory.validationError("Equipment does not belong to the same player as the hero.");
        }

        // Equip
        Map<EquipmentSlot, UUID> equipmentMap = hero.getEquipment();
        equipmentMap.put(slot, inventoryItemId); // Store inventoryItemId
        hero.setEquipment(equipmentMap);

        hero = heroRepository.save(hero);
        recalculateHeroStats(hero);

        log.info("Hero {} equipped InventoryItem {} in slot {}", heroId, inventoryItemId, slot);
        return hero;
    }

    /**
     * Recalculates final hero stats by adding equipment bonuses to base stats.
     */
    public void recalculateHeroStats(Hero hero) {
        // 1. Reset final values to base values for existing stats
        for (HeroStats stat : hero.getStats()) {
            stat.setFinalValue(stat.getBaseValue());
        }

        // 2. Identify all equipped items
        List<UUID> equippedInventoryItemIds = hero.getEquipment().values().stream()
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toList());

        if (equippedInventoryItemIds.isEmpty()) {
            return;
        }

        // 3. Fetch InventoryItems
        Map<UUID, InventoryItem> equippedItems = inventoryItemRepository.findAllByIdIn(equippedInventoryItemIds)
                .stream()
                .collect(java.util.stream.Collectors.toMap(InventoryItem::getId, item -> item));

        // 4. Apply stats from each equipped item
        for (UUID invItemId : equippedInventoryItemIds) {
            InventoryItem invItem = equippedItems.get(invItemId);
            if (invItem != null && invItem.getMetadata() != null && invItem.getMetadata().containsKey("equipmentId")) {
                String equipmentIdStr = (String) invItem.getMetadata().get("equipmentId");
                // The metadata stores the ID of the Equipment DEFINITION (Instance in
                // 'equipment' table)
                // If 'equipmentId' refers to the ID in the 'equipment' table:
                try {
                    UUID equipmentDefId = UUID.fromString(equipmentIdStr);
                    Equipment equipment = equipmentRepository.findById(equipmentDefId).orElse(null);
                    if (equipment != null) {
                        applyEquipmentStats(hero, equipment);
                    }
                } catch (IllegalArgumentException e) {
                    log.error("Invalid equipmentId UUID in metadata for inventory item {}: {}", invItemId,
                            equipmentIdStr);
                }
            }
        }
    }

    private void applyEquipmentStats(Hero hero, Equipment equipment) {
        // Apply base stats
        if (equipment.getBaseStats() != null) {
            for (var stat : equipment.getBaseStats()) {
                addStatBonus(hero, stat.getStatType(), stat.getValue());
            }
        }

        // Apply random stats
        if (equipment.getRandomStats() != null) {
            for (var stat : equipment.getRandomStats()) {
                addStatBonus(hero, stat.getStatType(), stat.getValue());
            }
        }
    }

    private void addStatBonus(Hero hero, StatType statType, Double value) {
        if (value == null || value == 0)
            return;

        // Try to find existing stat
        boolean found = false;
        for (HeroStats heroStat : hero.getStats()) {
            if (heroStat.getStatType() == statType) {
                heroStat.setFinalValue(heroStat.getFinalValue() + value);
                found = true;
                break;
            }
        }

        // If not found, add new stat (Base=0, Final=Value)
        if (!found) {
            HeroStats newStat = HeroStats.builder()
                    .hero(hero)
                    .statType(statType)
                    .baseValue(0.0)
                    .finalValue(value)
                    .build();
            hero.getStats().add(newStat);
        }
    }
}
