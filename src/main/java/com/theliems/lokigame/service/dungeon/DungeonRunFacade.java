package com.theliems.lokigame.service.dungeon;

import com.theliems.lokigame.model.dto.battle.BattleSimulateResponse;
import com.theliems.lokigame.model.dto.dungeon.DungeonRunResponse;
import com.theliems.lokigame.service.battle.BattleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DungeonRunFacade {

    private final BattleService battleService;
    private final DungeonService dungeonService;

    @Transactional
    public DungeonRunResponse executeDungeonRun(UUID playerId, List<UUID> heroIds, UUID dungeonId) {
        log.info("Starting dungeon run for player {} in dungeon {} with heroes {}", playerId, dungeonId, heroIds);

        // 1. Simulate Battle
        BattleSimulateResponse battleResult = battleService.simulateBattle(heroIds, dungeonId);

        // 2. Grant rewards only if heroes won
        DungeonService.DungeonRunResult rewardResult = null;
        if ("HEROES".equals(battleResult.getWinner())) {
            rewardResult = dungeonService.grantRewards(playerId, dungeonId);
            log.info("Dungeon run victory! rewards granted for player {}", playerId);
        } else {
            log.info("Dungeon run defeat for player {}", playerId);
        }

        // 3. Map to unified response
        return buildUnifiedResponse(battleResult, rewardResult);
    }

    private DungeonRunResponse buildUnifiedResponse(BattleSimulateResponse battleResult,
            DungeonService.DungeonRunResult rewardResult) {
        DungeonRunResponse.DungeonRunResponseBuilder builder = DungeonRunResponse.builder()
                .dungeonId(battleResult.getDungeon().getId())
                .winner(battleResult.getWinner())
                .turns(battleResult.getTurns())
                .xpAwarded(battleResult.getXpAwarded())
                .levelUpResults(battleResult.getLevelUpResults())
                .battleLogs(battleResult.getLogs().stream()
                        .map(logEntry -> DungeonRunResponse.BattleLogEntry.builder()
                                .turn(logEntry.getTurn())
                                .message(logEntry.getMessage())
                                .build())
                        .collect(Collectors.toList()));

        if (rewardResult != null) {
            builder.rewards(rewardResult.getRewards().stream()
                    .map(reward -> DungeonRunResponse.RewardResponse.builder()
                            .type(reward.getType())
                            .amount(reward.getAmount())
                            .equipmentId(reward.getItemId())
                            .name(reward.getName())
                            .build())
                    .collect(Collectors.toList()));
        }

        return builder.build();
    }
}
