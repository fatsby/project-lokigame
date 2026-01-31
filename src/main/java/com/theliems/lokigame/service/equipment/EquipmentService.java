package com.theliems.lokigame.service.equipment;

import com.theliems.lokigame.generator.EquipmentGenerator;
import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.entity.inventory.EquipmentItem;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.enums.EquipmentType;
import com.theliems.lokigame.repository.inventory.EquipmentItemRepository;
import com.theliems.lokigame.repository.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service for generating and managing equipment items.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentService {

        private final EquipmentGenerator equipmentGenerator;
        private final EquipmentItemRepository equipmentItemRepository;
        private final PlayerRepository playerRepository;
        private final ExceptionFactory exceptionFactory;

        /**
         * Generates a new equipment item for a player.
         *
         * @param playerId      The player who will own the equipment
         * @param equipmentType The type of equipment to generate
         * @param playerLevel   Player level for scaling
         * @param dungeonLevel  Dungeon level for scaling
         * @return The generated and persisted EquipmentItem
         */
        @Transactional
        public EquipmentItem generateEquipment(UUID playerId, EquipmentType equipmentType,
                        Integer playerLevel, Integer dungeonLevel) {
                Player player = playerRepository.findById(playerId)
                                .orElseThrow(() -> exceptionFactory.resourceNotFound("Player", playerId));

                // Generate and save in one step
                EquipmentItem equipmentItem = equipmentGenerator.generateEquipment(
                                player, equipmentType, playerLevel, dungeonLevel);
                equipmentItem = equipmentItemRepository.save(equipmentItem);

                log.info("Generated equipment (ID: {}) for player {}: {} (Rarity: {}, Level: {})",
                                equipmentItem.getId(), playerId, equipmentItem.getEquipmentType(),
                                equipmentItem.getRarity(), equipmentItem.getLevel());

                return equipmentItem;
        }
}
