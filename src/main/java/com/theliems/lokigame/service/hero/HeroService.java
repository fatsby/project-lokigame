package com.theliems.lokigame.service.hero;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.model.entity.hero.HeroStats;
import com.theliems.lokigame.model.entity.equipment.Equipment;
import com.theliems.lokigame.model.enums.EquipmentSlot;
import com.theliems.lokigame.model.enums.StatType;
import com.theliems.lokigame.repository.equipment.EquipmentRepository;
import com.theliems.lokigame.repository.hero.HeroRepository;
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

    public List<Hero> getPlayerHeroes(UUID playerId) {
        List<Hero> heroes = heroRepository.findByPlayerIdFull(playerId);
        // Recalculate final stats with equipment
        heroes.forEach(this::recalculateHeroStats);
        return heroes;
    }

    private final ExceptionFactory exceptionFactory;

    public Hero getHeroById(UUID heroId) {
        Hero hero = heroRepository.findById(heroId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Hero", heroId));
        recalculateHeroStats(hero);
        return hero;
    }

    @Transactional
    public Hero equipItem(UUID heroId, EquipmentSlot slot, UUID equipmentId) {
        Hero hero = heroRepository.findById(heroId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Hero", heroId));

        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Equipment", equipmentId));

        // Validate ownership
        if (!equipment.getOwner().getPlayerId().equals(hero.getOwner().getPlayerId())) {
            throw exceptionFactory.validationError("Equipment does not belong to the same player");
        }

        // Equip
        Map<EquipmentSlot, UUID> equipmentMap = hero.getEquipment();
        equipmentMap.put(slot, equipmentId);
        hero.setEquipment(equipmentMap);

        hero = heroRepository.save(hero);
        recalculateHeroStats(hero);

        log.info("Hero {} equipped {} in slot {}", heroId, equipmentId, slot);
        return hero;
    }

    /**
     * Recalculates final hero stats by adding equipment bonuses to base stats.
     */
    private void recalculateHeroStats(Hero hero) {
        // Reset final values to base values
        for (HeroStats stat : hero.getStats()) {
            stat.setFinalValue(stat.getBaseValue());
        }

        // Add equipment bonuses
        Map<EquipmentSlot, UUID> equipmentMap = hero.getEquipment();
        for (Map.Entry<EquipmentSlot, UUID> entry : equipmentMap.entrySet()) {
            UUID equipmentId = entry.getValue();
            if (equipmentId != null) {
                Equipment equipment = equipmentRepository.findById(equipmentId).orElse(null);
                if (equipment != null) {
                    applyEquipmentStats(hero, equipment);
                }
            }
        }
    }

    private void applyEquipmentStats(Hero hero, Equipment equipment) {
        // Apply base stats
        for (var stat : equipment.getBaseStats()) {
            addStatBonus(hero, stat.getStatType(), stat.getValue());
        }

        // Apply random stats
        for (var stat : equipment.getRandomStats()) {
            addStatBonus(hero, stat.getStatType(), stat.getValue());
        }
    }

    private void addStatBonus(Hero hero, StatType statType, Double value) {
        for (HeroStats heroStat : hero.getStats()) {
            if (heroStat.getStatType() == statType) {
                heroStat.setFinalValue(heroStat.getFinalValue() + value);
                break;
            }
        }
    }
}
