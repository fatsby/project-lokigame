package com.theliems.lokigame.service.dungeon;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.entity.dungeon.DropTable;
import com.theliems.lokigame.model.entity.dungeon.Dungeon;
import com.theliems.lokigame.model.entity.inventory.EquipmentItem;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.repository.dungeon.DungeonRepository;
import com.theliems.lokigame.repository.player.PlayerRepository;
import com.theliems.lokigame.service.equipment.EquipmentService;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service for dungeon runs and rewards.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DungeonService {

    private final DungeonRepository dungeonRepository;
    private final PlayerRepository playerRepository;
    private final EquipmentService equipmentService;
    private final ExceptionFactory exceptionFactory;

    public List<Dungeon> getAllDungeons() {
        return dungeonRepository.findAll();
    }

    public Dungeon getDungeonById(UUID dungeonId) {
        return dungeonRepository.findById(dungeonId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Dungeon", dungeonId));
    }

    @Transactional
    DungeonRunResult grantRewards(UUID playerId, UUID dungeonId) {
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Player", playerId));

        Dungeon dungeon = dungeonRepository.findById(dungeonId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Dungeon", dungeonId));

        DropTable dropTable = dungeon.getDropTable();
        if (dropTable == null) {
            throw exceptionFactory.validationError("Dungeon has no drop table");
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<Reward> rewards = new ArrayList<>();

        // 1. Calculate and Persist Gold Reward
        long goldReward = (long) (dropTable.getBaseGold() * dropTable.getGoldMultiplier() * dungeon.getLevel());
        player.setGold(player.getGold() + goldReward);
        playerRepository.save(player);

        rewards.add(Reward.builder()
                .type("GOLD")
                .amount(goldReward)
                .build());

        // 2. Roll for Equipment
        if (dropTable.getEquipmentDropChance() != null && random.nextDouble() < dropTable.getEquipmentDropChance()) {
            // Pick a random equipment type
            com.theliems.lokigame.model.enums.EquipmentType[] types = com.theliems.lokigame.model.enums.EquipmentType
                    .values();
            com.theliems.lokigame.model.enums.EquipmentType randomType = types[random.nextInt(types.length)];

            // Generate and persist equipment (now returns EquipmentItem directly)
            EquipmentItem equipmentItem = equipmentService.generateEquipment(playerId, randomType, dungeon.getLevel(),
                    dungeon.getLevel());

            rewards.add(Reward.builder()
                    .type("EQUIPMENT")
                    .amount(1L)
                    .itemId(equipmentItem.getId())
                    .name(equipmentItem.getDisplayName())
                    .build());
        }

        // 3. Roll for Materials (Placeholder - Logic not fully implemented yet)
        if (dropTable.getMaterialDropChance() != null && random.nextDouble() < dropTable.getMaterialDropChance()) {
            // For now, we don't persist materials
            // This can be implemented when a MaterialItem subclass is added
        }

        log.info("Player {} completed dungeon {} and received {} rewards", playerId, dungeonId, rewards.size());

        return DungeonRunResult.builder()
                .dungeonId(dungeonId)
                .rewards(rewards)
                .build();
    }

    @Data
    @Builder
    public static class DungeonRunResult {
        private UUID dungeonId;
        private List<Reward> rewards;
    }

    @Data
    @Builder
    public static class Reward {
        private String type; // GOLD, EQUIPMENT
        private Long amount;
        private UUID itemId;
        private String name;
    }
}
