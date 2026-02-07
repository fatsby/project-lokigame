package com.theliems.lokigame.service.dungeon;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.dto.battle.BattleSimulateResponse;
import com.theliems.lokigame.model.dto.dungeon.DungeonRunResponse;
import com.theliems.lokigame.model.dto.dungeon.DungeonRunResult;
import com.theliems.lokigame.model.entity.dungeon.Dungeon;
import com.theliems.lokigame.service.battle.BattleService;
import com.theliems.lokigame.service.hero.HeroService;
import com.theliems.lokigame.service.player.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Facade for executing complete dungeon runs.
 * Orchestrates dungeon generation, battle simulation, and reward distribution.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DungeonRunFacade {

    private final BattleService battleService;
    private final DungeonService dungeonService;
    private final PlayerService playerService;
    private final HeroService heroService;
    private final ExceptionFactory exceptionFactory;

    /**
     * Execute a complete dungeon run with procedural generation.
     * 
     * Flow:
     * 1. Validate level access
     * 2. Get/create seed and generate dungeon
     * 3. Simulate battle
     * 4. On victory: grant rewards, mark cleared, update progression
     * 5. On defeat: seed remains for retry
     * 
     * @param heroIds      List of hero UUIDs to use in battle
     * @param dungeonLevel The dungeon level to attempt
     * @return Complete dungeon run response
     */
    @Transactional
    public DungeonRunResponse executeDungeonRun(List<UUID> heroIds, int dungeonLevel) {
        // Validation check to ensure all heroes are alive
        heroService.areAllHeroesAlive(heroIds);

        UUID playerId = playerService.getCurrentPlayer().getPlayerId();

        // 1. Get or generate dungeon (validates level access, creates/retrieves seed
        // which determines the world)
        Dungeon dungeon = dungeonService.getOrGenerateDungeon(playerId, dungeonLevel);

        log.info("Starting dungeon run for player {} at level {} in world {} (seed: {})",
                playerId, dungeonLevel, dungeon.getWorldId(), dungeon.getSeed());
        log.info("Generated dungeon '{}' with {} monsters", dungeon.getName(), dungeon.getMonsters().size());

        // 2. Simulate Battle
        BattleSimulateResponse battleResult = battleService.simulateBattle(heroIds, dungeon);

        // 3. Grant rewards only if heroes won
        DungeonRunResult rewardResult = null;
        if ("HEROES".equals(battleResult.getWinner())) {
            rewardResult = dungeonService.grantRewards(playerId, dungeon);
            dungeonService.markDungeonCleared(playerId, dungeon);
            log.info("Dungeon run victory! Rewards granted and progression updated for player {}", playerId);
        } else {
            log.info("Dungeon run defeat for player {} - seed remains for retry", playerId);
            heroService.updateHeroAliveStatus(heroIds, false);
        }

        // 4. Map to unified response
        return buildUnifiedResponse(battleResult, rewardResult, dungeon);
    }

    private DungeonRunResponse buildUnifiedResponse(BattleSimulateResponse battleResult,
            DungeonRunResult rewardResult, Dungeon dungeon) {
        DungeonRunResponse.DungeonRunResponseBuilder builder = DungeonRunResponse.builder()
                .dungeonId(dungeon.getId())
                .dungeonLevel(dungeon.getLevel())
                .dungeonName(dungeon.getName())
                .winner(battleResult.getWinner())
                .turns(battleResult.getTurns())
                .xpAwarded(battleResult.getXpAwarded())
                .levelUpResults(battleResult.getLevelUpResults())
                .battleLogs(battleResult.getLogs());

        if (rewardResult != null) {
            builder.rewards(rewardResult.getRewards());
        }

        return builder.build();
    }
}
