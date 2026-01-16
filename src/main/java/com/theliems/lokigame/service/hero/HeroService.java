package com.theliems.lokigame.service.hero;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.infrastructure.exception.errorCategories.HeroError;
import com.theliems.lokigame.infrastructure.security.SecurityContextService;
import com.theliems.lokigame.mapper.hero.HeroMapper;
import com.theliems.lokigame.mapper.inventory.InventoryItemMapper;
import com.theliems.lokigame.model.dto.hero.HeroResponseDTO;
import com.theliems.lokigame.model.dto.hero.HeroSacrificeDTO;
import com.theliems.lokigame.model.dto.inventory.InventoryItemDTO;
import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.model.entity.inventory.InventoryItem;
import com.theliems.lokigame.model.enums.EquipmentSlot;
import com.theliems.lokigame.repository.hero.HeroRepository;
import com.theliems.lokigame.service.inventory.InventoryItemService;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Facade service for hero-related operations.
 * Handles authentication context and delegates to specialized services.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HeroService {

    HeroGenerationService heroGenerationService;
    HeroRepository heroRepository;
    HeroMapper heroMapper;
    InventoryItemMapper inventoryItemMapper;
    InventoryItemService inventoryItemService;
    SecurityContextService securityContextService;
    ExceptionFactory exceptionFactory;

    /**
     * Summon a new hero for the currently authenticated player.
     *
     * @return the newly summoned Hero
     */
    public HeroResponseDTO summonHeroForCurrentPlayer() {
        UUID playerUUID = securityContextService.getCurrentPlayerId();
        Hero summonedHero = heroGenerationService.summonHero(playerUUID);
        HeroResponseDTO dto = heroMapper.toDTO(summonedHero);
        populateEquipment(dto, summonedHero.getEquipment());
        return dto;
    }

    /**
     * Get all heroes owned by the currently authenticated player.
     * Uses batch fetching for equipment items to avoid N+1 query problem.
     *
     * @return list of heroes owned by the current player
     */
    public List<HeroResponseDTO> getHeroesForCurrentPlayer() {
        UUID playerUUID = securityContextService.getCurrentPlayerId();
        List<Hero> heroes = heroRepository.findByOwnerId(playerUUID);

        if (heroes.isEmpty()) {
            return List.of();
        }

        // Collect all equipment item UUIDs across all heroes
        Set<UUID> allEquipmentIds = heroes.stream()
                .filter(h -> h.getEquipment() != null)
                .flatMap(h -> h.getEquipment().values().stream())
                .collect(Collectors.toSet());

        // Batch fetch all equipment items in a SINGLE query
        Map<UUID, InventoryItem> itemsById = inventoryItemService.getItemsByIds(allEquipmentIds);

        // Map heroes to DTOs and populate equipment from the pre-fetched map
        return heroes.stream()
                .map(hero -> {
                    HeroResponseDTO dto = heroMapper.toDTO(hero);
                    populateEquipmentFromMap(dto, hero.getEquipment(), itemsById);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public HeroResponseDTO absorbHeroPower(UUID targetHeroId, HeroSacrificeDTO sacrificeDTO) {
        List<UUID> heroesToAbsorbIds = sacrificeDTO.getHeroesToSacrificeIds();
        log.info("Absorbing hero power for target hero: {}", targetHeroId);
        log.info("Heroes to absorb: {}", heroesToAbsorbIds);

        if (heroesToAbsorbIds == null || heroesToAbsorbIds.isEmpty()) {
            throw exceptionFactory.createNotFoundException("heroesToAbsorbIds", heroesToAbsorbIds,
                    HeroError.HERO_SACRIFICE_INVALID);
        }
        // prevent sacrificing the target hero
        if (heroesToAbsorbIds.contains(targetHeroId)) {
            throw exceptionFactory.createNotFoundException("targetHeroId", targetHeroId, HeroError.HERO_SELF_SACRIFICE);
        }

        UUID currentPlayerId = securityContextService.getCurrentPlayerId();
        Hero targetHero = heroRepository.findByOwnerIdAndId(currentPlayerId, targetHeroId)
                .orElseThrow(
                        () -> exceptionFactory.createNotFoundException("targetHeroId", targetHeroId,
                                HeroError.HERO_NOT_FOUND));

        List<Object[]> rawStats = heroRepository.aggregateHeroStats(currentPlayerId, heroesToAbsorbIds);

        if (rawStats.isEmpty()) {
            throw exceptionFactory.createNotFoundException("heroesToAbsorbIds", heroesToAbsorbIds,
                    HeroError.HERO_NOT_FOUND);
        }

        Map<String, Double> currentStats = targetHero.getStats();

        for (Object[] row : rawStats) {
            String statName = (String) row[0];
            Double bonusValue = ((Number) row[1]).doubleValue();
            currentStats.merge(statName, bonusValue, Double::sum);
        }

        heroRepository.toggleHeroesAlive(currentPlayerId, heroesToAbsorbIds, false);

        targetHero.setStats(currentStats);

        HeroResponseDTO dto = heroMapper.toDTO(heroRepository.save(targetHero));
        populateEquipment(dto, targetHero.getEquipment());
        return dto;
    }

    /**
     * Populates the equipment field of a HeroResponseDTO with full InventoryItemDTO
     * objects.
     *
     * @param dto            the HeroResponseDTO to populate
     * @param equipmentUuids the map of EquipmentSlot to InventoryItem UUIDs from
     *                       the Hero entity
     */
    private void populateEquipment(HeroResponseDTO dto, Map<EquipmentSlot, UUID> equipmentUuids) {
        if (equipmentUuids == null || equipmentUuids.isEmpty()) {
            dto.setEquipment(new EnumMap<>(EquipmentSlot.class));
            return;
        }

        Map<EquipmentSlot, InventoryItemDTO> equipmentDTOs = new EnumMap<>(EquipmentSlot.class);

        for (Map.Entry<EquipmentSlot, UUID> entry : equipmentUuids.entrySet()) {
            EquipmentSlot slot = entry.getKey();
            UUID itemId = entry.getValue();

            try {
                InventoryItem item = inventoryItemService.getItemById(itemId);
                InventoryItemDTO itemDTO = inventoryItemMapper.toDTO(item);
                equipmentDTOs.put(slot, itemDTO);
            } catch (Exception e) {
                log.warn("Failed to fetch equipment item {} for slot {}: {}", itemId, slot, e.getMessage());
                // Skip this slot if item not found, but log for debugging
            }
        }

        dto.setEquipment(equipmentDTOs);
    }

    /**
     * Populates equipment from a pre-fetched items map (batch-optimized).
     * Used when loading multiple heroes to avoid N+1 queries.
     */
    private void populateEquipmentFromMap(HeroResponseDTO dto, Map<EquipmentSlot, UUID> equipmentUuids,
            Map<UUID, InventoryItem> itemsById) {
        if (equipmentUuids == null || equipmentUuids.isEmpty()) {
            dto.setEquipment(new EnumMap<>(EquipmentSlot.class));
            return;
        }

        Map<EquipmentSlot, InventoryItemDTO> equipmentDTOs = new EnumMap<>(EquipmentSlot.class);

        for (Map.Entry<EquipmentSlot, UUID> entry : equipmentUuids.entrySet()) {
            EquipmentSlot slot = entry.getKey();
            UUID itemId = entry.getValue();
            InventoryItem item = itemsById.get(itemId);

            if (item != null) {
                equipmentDTOs.put(slot, inventoryItemMapper.toDTO(item));
            } else {
                log.warn("Equipment item {} not found in pre-fetched map for slot {}", itemId, slot);
            }
        }

        dto.setEquipment(equipmentDTOs);
    }
}
