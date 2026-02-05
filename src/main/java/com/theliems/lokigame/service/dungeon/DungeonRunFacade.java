package com.theliems.lokigame.service.dungeon;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.dto.battle.BattleSimulateResponse;
import com.theliems.lokigame.model.dto.dungeon.DungeonRunResponse;
import com.theliems.lokigame.model.dto.dungeon.DungeonRunResult;
import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.repository.hero.HeroRepository;
import com.theliems.lokigame.service.battle.BattleService;
import com.theliems.lokigame.service.hero.HeroService;
import com.theliems.lokigame.service.player.PlayerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DungeonRunFacade {

    private final BattleService battleService;
    private final DungeonService dungeonService;
    private final PlayerService playerService;
    private final HeroService heroService;
    private final ExceptionFactory exceptionFactory;

    @Transactional
    public DungeonRunResponse executeDungeonRun(List<UUID> heroIds, UUID dungeonId) {
        //validation check to ensure all heroes are alive
        heroService.areAllHeroesAlive(heroIds);

        UUID playerId = playerService.getCurrentPlayer().getPlayerId();
        log.info("Starting dungeon run for player {} in dungeon {} with heroes {}", playerId, dungeonId, heroIds);

        // 1. Simulate Battle
        BattleSimulateResponse battleResult = battleService.simulateBattle(heroIds, dungeonId);

        // 2. Grant rewards only if heroes won
        DungeonRunResult rewardResult = null;
        if ("HEROES".equals(battleResult.getWinner())) {
            rewardResult = dungeonService.grantRewards(playerId, dungeonId);
            log.info("Dungeon run victory! rewards granted for player {}", playerId);
        } else {
            log.info("Dungeon run defeat for player {}", playerId);
            heroService.updateHeroAliveStatus(heroIds, false);
        }

        // 3. Map to unified response
        return buildUnifiedResponse(battleResult, rewardResult);
    }

    private DungeonRunResponse buildUnifiedResponse(BattleSimulateResponse battleResult,
            DungeonRunResult rewardResult) {
        DungeonRunResponse.DungeonRunResponseBuilder builder = DungeonRunResponse.builder()
                .dungeonId(battleResult.getDungeon().getId())
                .winner(battleResult.getWinner())
                .turns(battleResult.getTurns())
                .xpAwarded(battleResult.getXpAwarded())
                .levelUpResults(battleResult.getLevelUpResults())
                .battleLogs(battleResult.getLogs()); // Direct assignment - same DTO type

        if (rewardResult != null) {
            builder.rewards(rewardResult.getRewards()); // Direct assignment - same DTO type
        }

        return builder.build();
    }
}
