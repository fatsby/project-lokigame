package com.theliems.lokigame.service.equipment;

import com.theliems.lokigame.generator.EquipmentGenerator;
import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.entity.equipment.Equipment;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.enums.EquipmentType;
import com.theliems.lokigame.repository.equipment.EquipmentRepository;
import com.theliems.lokigame.repository.player.PlayerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentService {

    private final EquipmentGenerator equipmentGenerator;
    private final EquipmentRepository equipmentRepository;
    private final PlayerRepository playerRepository;

    private final ExceptionFactory exceptionFactory;

    @Transactional
    public Equipment generateEquipment(UUID playerId, EquipmentType equipmentType, Integer playerLevel, Integer dungeonLevel) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Player", playerId));

        Equipment equipment = equipmentGenerator.generateEquipment(equipmentType, playerLevel, dungeonLevel);
        equipment.setOwner(player);

        equipment = equipmentRepository.save(equipment);

        log.info("Generated equipment for player {}: {} {} (Rarity: {}, Level: {})",
                playerId, equipment.getRarity(), equipment.getEquipmentType(),
                equipment.getRarity(), equipment.getLevel());

        return equipment;
    }

    public List<Equipment> getPlayerEquipment(UUID playerId) {
        return equipmentRepository.findByOwner_PlayerId(playerId);
    }

    public Equipment getEquipmentById(UUID equipmentId) {
        return equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Equipment", equipmentId));
    }
}
