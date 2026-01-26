package com.theliems.lokigame.controller.equipment;

import com.theliems.lokigame.model.dto.equipment.EquipmentGenerateRequest;
import com.theliems.lokigame.model.dto.equipment.EquipmentResponse;
import com.theliems.lokigame.model.entity.equipment.Equipment;
import com.theliems.lokigame.model.entity.equipment.EquipmentStat;
import com.theliems.lokigame.service.equipment.EquipmentService;
import com.theliems.lokigame.service.player.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final PlayerService playerService;

    @PostMapping("/generate")
    public ResponseEntity<EquipmentResponse> generateEquipment(@RequestBody EquipmentGenerateRequest request) {
        UUID playerId = playerService.getCurrentPlayer().getPlayerId();
        Equipment equipment = equipmentService.generateEquipment(
                playerId,
                request.getEquipmentType(),
                request.getPlayerLevel(),
                request.getDungeonLevel()
        );
        return ResponseEntity.ok(mapToResponse(equipment));
    }

    @GetMapping("/my-equipment")
    public ResponseEntity<List<EquipmentResponse>> getMyEquipment() {
        UUID playerId = playerService.getCurrentPlayer().getPlayerId();
        List<Equipment> equipmentList = equipmentService.getPlayerEquipment(playerId);
        List<EquipmentResponse> responses = equipmentList.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    private EquipmentResponse mapToResponse(Equipment equipment) {
        List<EquipmentResponse.EquipmentStatResponse> baseStats = equipment.getBaseStats().stream()
                .map(stat -> EquipmentResponse.EquipmentStatResponse.builder()
                        .statType(stat.getStatType().name())
                        .value(stat.getValue())
                        .build())
                .collect(Collectors.toList());

        List<EquipmentResponse.EquipmentStatResponse> randomStats = equipment.getRandomStats().stream()
                .map(stat -> EquipmentResponse.EquipmentStatResponse.builder()
                        .statType(stat.getStatType().name())
                        .value(stat.getValue())
                        .build())
                .collect(Collectors.toList());

        return EquipmentResponse.builder()
                .id(equipment.getId())
                .equipmentType(equipment.getEquipmentType())
                .rarity(equipment.getRarity())
                .level(equipment.getLevel())
                .baseStats(baseStats)
                .randomStats(randomStats)
                .build();
    }
}
