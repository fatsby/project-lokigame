package com.theliems.lokigame.service.hero;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.model.entity.hero.HeroStats;
import com.theliems.lokigame.model.entity.inventory.EquipmentItem;
import com.theliems.lokigame.model.enums.EquipmentSlot;
import com.theliems.lokigame.model.enums.StatType;
import com.theliems.lokigame.repository.inventory.EquipmentItemRepository;
import com.theliems.lokigame.repository.hero.HeroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for hero management and stat calculations.
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class HeroService {

    private final HeroRepository heroRepository;
    private final EquipmentItemRepository equipmentItemRepository;
    private final ExceptionFactory exceptionFactory;

    public List<Hero> getPlayerHeroes(UUID playerId) {
        List<Hero> heroes = heroRepository.findByPlayerIdFull(playerId);
        // Recalculate final stats with equipment
        heroes.forEach(this::recalculateHeroStats);
        return heroes;
    }

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
    public Hero equipItem(UUID heroId, EquipmentSlot slot, UUID equipmentItemId) {
        Hero hero = heroRepository.findById(heroId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Hero", heroId));

        EquipmentItem equipmentItem = equipmentItemRepository.findById(equipmentItemId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("EquipmentItem", equipmentItemId));

        // Validate ownership: Check if equipment owner matches hero owner
        if (!equipmentItem.getOwner().getPlayerId().equals(hero.getOwner().getPlayerId())) {
            throw exceptionFactory.validationError("Equipment does not belong to the same player as the hero.");
        }

        // Equip
        Map<EquipmentSlot, UUID> equipmentMap = hero.getEquipment();
        equipmentMap.put(slot, equipmentItemId);
        hero.setEquipment(equipmentMap);

        hero = heroRepository.save(hero);
        recalculateHeroStats(hero);

        log.info("Hero {} equipped EquipmentItem {} in slot {}", heroId, equipmentItemId, slot);
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
        List<UUID> equippedItemIds = hero.getEquipment().values().stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (equippedItemIds.isEmpty()) {
            return;
        }

        // 3. Fetch EquipmentItems directly (with stats already loaded via EAGER)
        List<EquipmentItem> equippedItems = equipmentItemRepository.findAllById(equippedItemIds);

        // 4. Apply stats from each equipped item
        for (EquipmentItem item : equippedItems) {
            applyEquipmentStats(hero, item);
        }
    }

    public void areAllHeroesAlive(List<UUID> heroIds){
        if (!heroRepository.areAllHeroesAlive(heroIds)) {
            throw exceptionFactory.validationError("One or more heroes are dead.");
        }
    }

    public void updateHeroAliveStatus(List<UUID> heroIds, boolean status) {
        heroRepository.updateAliveStatus(heroIds, status);
    }

    private void applyEquipmentStats(Hero hero, EquipmentItem equipment) {
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
