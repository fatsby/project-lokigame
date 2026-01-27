package com.theliems.lokigame.service.dungeon;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.entity.dungeon.DropTable;
import com.theliems.lokigame.model.entity.dungeon.Dungeon;
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
    public DungeonRunResult runDungeon(UUID playerId, UUID dungeonId) {
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

        // Calculate gold reward
        long goldReward = (long) (dropTable.getBaseGold() * dropTable.getGoldMultiplier() * dungeon.getLevel());
        rewards.add(Reward.builder()
                .type("GOLD")
                .amount(goldReward)
                .build());

        // Roll for materials (placeholder)
        if (random.nextDouble() < dropTable.getMaterialDropChance()) {
            rewards.add(Reward.builder()
                    .type("MATERIAL")
                    .amount(1L)
                    .build());
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
        private String type; // GOLD, MATERIAL
        private Long amount;
    }
}
