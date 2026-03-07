package com.theliems.lokigame.service.dungeon;

import com.theliems.lokigame.model.dto.battle.BattleSimulateResponse;
import com.theliems.lokigame.model.dto.battle.BattleUnitState;
import com.theliems.lokigame.model.dto.dungeon.DungeonReward;
import com.theliems.lokigame.model.dto.dungeon.DungeonRunResponse;
import com.theliems.lokigame.model.dto.dungeon.DungeonRunResult;
import com.theliems.lokigame.model.entity.dungeon.Dungeon;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.service.battle.BattleService;
import com.theliems.lokigame.service.hero.HeroService;
import com.theliems.lokigame.service.player.PlayerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DungeonRunFacadeTest {

    @Mock
    private BattleService battleService;

    @Mock
    private DungeonService dungeonService;

    @Mock
    private PlayerService playerService;

    @Mock
    private HeroService heroService;

    @InjectMocks
    private DungeonRunFacade dungeonRunFacade;

    @Test
    void executeDungeonRun_victoryWithMixedCasualties_syncsCasualtiesAndGrantsRewards() {
        UUID playerId = UUID.randomUUID();
        UUID aliveHeroId = UUID.randomUUID();
        UUID deadHeroId = UUID.randomUUID();
        List<UUID> heroIds = List.of(aliveHeroId, deadHeroId);

        Dungeon dungeon = buildDungeon();
        List<BattleUnitState> heroStates = List.of(
                BattleUnitState.builder().id(aliveHeroId).isHero(true).isAlive(true).build(),
                BattleUnitState.builder().id(deadHeroId).isHero(true).isAlive(false).build()
        );

        BattleSimulateResponse battleResult = BattleSimulateResponse.builder()
                .winner("HEROES")
                .turns(8)
                .heroes(heroStates)
                .logs(List.of())
                .xpAwarded(100)
                .levelUpResults(List.of())
                .build();

        DungeonRunResult rewardResult = DungeonRunResult.builder()
                .dungeonId(dungeon.getId())
                .rewards(List.of(DungeonReward.builder().type("GOLD").amount(25L).build()))
                .build();

        when(playerService.getCurrentPlayer()).thenReturn(Player.builder().playerId(playerId).build());
        when(dungeonService.getOrGenerateDungeon(playerId, 3)).thenReturn(dungeon);
        when(battleService.simulateBattle(heroIds, dungeon)).thenReturn(battleResult);
        when(dungeonService.grantRewards(playerId, dungeon)).thenReturn(rewardResult);

        DungeonRunResponse response = dungeonRunFacade.executeDungeonRun(heroIds, 3);

        verify(heroService).areAllHeroesAlive(heroIds);
        verify(heroService).syncAliveStatusFromBattle(heroStates);
        verify(dungeonService).grantRewards(playerId, dungeon);
        verify(dungeonService).markDungeonCleared(playerId, dungeon);
        verify(heroService, never()).updateHeroAliveStatus(anyList(), org.mockito.ArgumentMatchers.anyBoolean());

        assertThat(response.getWinner()).isEqualTo("HEROES");
        assertThat(response.getRewards()).hasSize(1);
    }

    @Test
    void executeDungeonRun_monstersWin_syncsCasualtiesAndDoesNotGrantRewards() {
        UUID playerId = UUID.randomUUID();
        UUID aliveHeroId = UUID.randomUUID();
        UUID deadHeroId = UUID.randomUUID();
        List<UUID> heroIds = List.of(aliveHeroId, deadHeroId);

        Dungeon dungeon = buildDungeon();
        List<BattleUnitState> heroStates = List.of(
                BattleUnitState.builder().id(aliveHeroId).isHero(true).isAlive(true).build(),
                BattleUnitState.builder().id(deadHeroId).isHero(true).isAlive(false).build()
        );

        BattleSimulateResponse battleResult = BattleSimulateResponse.builder()
                .winner("MONSTERS")
                .turns(5)
                .heroes(heroStates)
                .logs(List.of())
                .xpAwarded(0)
                .levelUpResults(List.of())
                .build();

        when(playerService.getCurrentPlayer()).thenReturn(Player.builder().playerId(playerId).build());
        when(dungeonService.getOrGenerateDungeon(playerId, 3)).thenReturn(dungeon);
        when(battleService.simulateBattle(heroIds, dungeon)).thenReturn(battleResult);

        DungeonRunResponse response = dungeonRunFacade.executeDungeonRun(heroIds, 3);

        verify(heroService).syncAliveStatusFromBattle(heroStates);
        verify(dungeonService, never()).grantRewards(any(), any());
        verify(dungeonService, never()).markDungeonCleared(any(), any());
        assertThat(response.getWinner()).isEqualTo("MONSTERS");
        assertThat(response.getRewards()).isNull();
    }

    @Test
    void executeDungeonRun_draw_syncsCasualtiesAndDoesNotGrantRewards() {
        UUID playerId = UUID.randomUUID();
        UUID heroId = UUID.randomUUID();
        List<UUID> heroIds = List.of(heroId);

        Dungeon dungeon = buildDungeon();
        List<BattleUnitState> heroStates = List.of(
                BattleUnitState.builder().id(heroId).isHero(true).isAlive(false).build()
        );

        BattleSimulateResponse battleResult = BattleSimulateResponse.builder()
                .winner("DRAW")
                .turns(100)
                .heroes(heroStates)
                .logs(List.of())
                .xpAwarded(0)
                .levelUpResults(List.of())
                .build();

        when(playerService.getCurrentPlayer()).thenReturn(Player.builder().playerId(playerId).build());
        when(dungeonService.getOrGenerateDungeon(playerId, 4)).thenReturn(dungeon);
        when(battleService.simulateBattle(heroIds, dungeon)).thenReturn(battleResult);

        DungeonRunResponse response = dungeonRunFacade.executeDungeonRun(heroIds, 4);

        verify(heroService).syncAliveStatusFromBattle(heroStates);
        verify(dungeonService, never()).grantRewards(any(), any());
        verify(dungeonService, never()).markDungeonCleared(any(), any());
        assertThat(response.getWinner()).isEqualTo("DRAW");
        assertThat(response.getRewards()).isNull();
    }

    private Dungeon buildDungeon() {
        return Dungeon.builder()
                .id(UUID.randomUUID())
                .name("Test Dungeon")
                .level(3)
                .worldId(UUID.randomUUID())
                .seed(123L)
                .build();
    }
}

