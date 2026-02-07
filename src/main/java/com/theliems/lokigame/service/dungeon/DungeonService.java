package com.theliems.lokigame.service.dungeon;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.entity.dungeon.DropTable;
import com.theliems.lokigame.model.entity.dungeon.Dungeon;
import com.theliems.lokigame.model.entity.dungeon.DungeonSeed;
import com.theliems.lokigame.model.entity.inventory.EquipmentItem;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.dto.dungeon.DungeonReward;
import com.theliems.lokigame.model.dto.dungeon.DungeonRunResult;
import com.theliems.lokigame.repository.player.PlayerRepository;
import com.theliems.lokigame.service.equipment.EquipmentService;
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
 * Refactored to work with procedurally generated dungeons.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DungeonService {

        private final PlayerRepository playerRepository;
        private final EquipmentService equipmentService;
        private final DungeonSeedService dungeonSeedService;
        private final DungeonGeneratorService dungeonGeneratorService;
        private final ExceptionFactory exceptionFactory;

        /**
         * Get or generate a dungeon for a player at a specific level.
         * The world is determined by the seed (either existing or newly rolled).
         * 
         * @param playerId The player's ID
         * @param level    The dungeon level
         * @return A procedurally generated Dungeon
         */
        public Dungeon getOrGenerateDungeon(UUID playerId, int level) {
                // This determines the world, validates level access, and creates/retrieves the
                // seed
                DungeonSeed seed = dungeonSeedService.getOrCreateSeed(playerId, level);

                // Generate dungeon from seed (deterministic)
                return dungeonGeneratorService.generateFromSeed(seed);
        }

        /**
         * Grant rewards to a player for completing a dungeon.
         * 
         * @param playerId The player's ID
         * @param dungeon  The completed dungeon
         * @return DungeonRunResult with reward details
         */
        @Transactional
        public DungeonRunResult grantRewards(UUID playerId, Dungeon dungeon) {
                Player player = playerRepository.findById(playerId)
                                .orElseThrow(() -> exceptionFactory.resourceNotFound("Player", playerId));

                DropTable dropTable = dungeon.getDropTable();
                if (dropTable == null) {
                        throw exceptionFactory.validationError("Dungeon has no drop table");
                }

                ThreadLocalRandom random = ThreadLocalRandom.current();
                List<DungeonReward> rewards = new ArrayList<>();

                // 1. Calculate and Persist Gold Reward
                long goldReward = (long) (dropTable.getBaseGold() * dropTable.getGoldMultiplier());
                player.setGold(player.getGold() + goldReward);
                playerRepository.save(player);

                rewards.add(DungeonReward.builder()
                                .type("GOLD")
                                .amount(goldReward)
                                .build());

                // 2. Roll for Equipment
                if (dropTable.getEquipmentDropChance() != null
                                && random.nextDouble() < dropTable.getEquipmentDropChance()) {
                        // Pick a random equipment type
                        com.theliems.lokigame.model.enums.EquipmentType[] types = com.theliems.lokigame.model.enums.EquipmentType
                                        .values();
                        com.theliems.lokigame.model.enums.EquipmentType randomType = types[random
                                        .nextInt(types.length)];

                        // Generate and persist equipment scaled to dungeon level
                        EquipmentItem equipmentItem = equipmentService.generateEquipment(playerId, randomType,
                                        dungeon.getLevel(),
                                        dungeon.getLevel());

                        rewards.add(DungeonReward.builder()
                                        .type("EQUIPMENT")
                                        .amount(1L)
                                        .itemId(equipmentItem.getId())
                                        .name(equipmentItem.getDisplayName())
                                        .build());
                }

                // 3. Roll for Materials (Placeholder - Logic not fully implemented yet)
                if (dropTable.getMaterialDropChance() != null
                                && random.nextDouble() < dropTable.getMaterialDropChance()) {
                        // For now, we don't persist materials
                        // This can be implemented when a MaterialItem subclass is added
                }

                log.info("Player {} completed dungeon (level {}) and received {} rewards",
                                playerId, dungeon.getLevel(), rewards.size());

                return DungeonRunResult.builder()
                                .dungeonId(dungeon.getId())
                                .rewards(rewards)
                                .build();
        }

        /**
         * Mark a dungeon as cleared and update player progression.
         * 
         * @param playerId The player's ID
         * @param dungeon  The cleared dungeon
         */
        @Transactional
        public void markDungeonCleared(UUID playerId, Dungeon dungeon) {
                dungeonSeedService.markCleared(playerId, dungeon.getWorldId(), dungeon.getLevel());
        }
}
