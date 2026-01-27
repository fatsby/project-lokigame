package com.theliems.lokigame.service.equipment;

import com.theliems.lokigame.generator.EquipmentGenerator;
import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.entity.equipment.Equipment;
import com.theliems.lokigame.model.entity.inventory.InventoryItem;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.enums.EquipmentType;
import com.theliems.lokigame.repository.equipment.EquipmentRepository;
import com.theliems.lokigame.repository.inventory.InventoryItemRepository;
import com.theliems.lokigame.repository.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentService {

        private final EquipmentGenerator equipmentGenerator;
        private final EquipmentRepository equipmentRepository;
        private final InventoryItemRepository inventoryItemRepository;
        private final PlayerRepository playerRepository;

        private final ExceptionFactory exceptionFactory;

        @Transactional
        public InventoryItem generateEquipment(UUID playerId, EquipmentType equipmentType, Integer playerLevel,
                        Integer dungeonLevel) {
                Player player = playerRepository.findById(playerId)
                                .orElseThrow(() -> exceptionFactory.resourceNotFound("Player", playerId));

                // Generate the Equipment definition (rolled stats)
                Equipment equipment = equipmentGenerator.generateEquipment(equipmentType, playerLevel, dungeonLevel);
                equipment = equipmentRepository.save(equipment); // Save the equipment definition

                // Create an InventoryItem instance for the player
                InventoryItem inventoryItem = InventoryItem.builder()
                                .owner(player)
                                .itemId(equipment.getId().toString()) // Use Equipment's ID as the item reference
                                .type(equipment.getEquipmentType().toItemType()) // Map EquipmentType to ItemType
                                .tier(equipment.getRarity().toItemTier()) // Map Rarity to ItemTier
                                .metadata(Map.of("equipmentId", equipment.getId().toString())) // Store reference to
                                                                                               // actual Equipment
                                                                                               // entity
                                .build();
                inventoryItem = inventoryItemRepository.save(inventoryItem);

                log.info("Generated equipment (InventoryItem ID: {}) for player {}: {} {} (Rarity: {}, Level: {})",
                                inventoryItem.getId(), playerId, equipment.getRarity(), equipment.getEquipmentType(),
                                equipment.getRarity(), equipment.getLevel());

                return inventoryItem;
        }
}
