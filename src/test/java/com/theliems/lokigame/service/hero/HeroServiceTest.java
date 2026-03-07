package com.theliems.lokigame.service.hero;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.dto.battle.BattleUnitState;
import com.theliems.lokigame.repository.hero.HeroRepository;
import com.theliems.lokigame.repository.inventory.EquipmentItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class HeroServiceTest {

    @Mock
    private HeroRepository heroRepository;

    @Mock
    private EquipmentItemRepository equipmentItemRepository;

    @Mock
    private ExceptionFactory exceptionFactory;

    @InjectMocks
    private HeroService heroService;

    @Test
    void syncAliveStatusFromBattle_updatesAliveAndDeadHeroesSeparately() {
        UUID aliveHeroId = UUID.randomUUID();
        UUID deadHeroId = UUID.randomUUID();

        List<BattleUnitState> states = List.of(
                BattleUnitState.builder().id(aliveHeroId).isHero(true).isAlive(true).build(),
                BattleUnitState.builder().id(deadHeroId).isHero(true).isAlive(false).build(),
                BattleUnitState.builder().id(UUID.randomUUID()).isHero(false).isAlive(false).build()
        );

        heroService.syncAliveStatusFromBattle(states);

        verify(heroRepository).updateAliveStatus(List.of(aliveHeroId), true);
        verify(heroRepository).updateAliveStatus(List.of(deadHeroId), false);
    }

    @Test
    void syncAliveStatusFromBattle_noOpOnNullOrEmpty() {
        heroService.syncAliveStatusFromBattle(null);
        heroService.syncAliveStatusFromBattle(List.of());

        verify(heroRepository, never()).updateAliveStatus(any(), eq(true));
        verify(heroRepository, never()).updateAliveStatus(any(), eq(false));
    }

    @Test
    void syncAliveStatusFromBattle_updatesOnlyAlivePartitionWhenNoDeadHeroes() {
        UUID aliveHeroId = UUID.randomUUID();

        List<BattleUnitState> states = List.of(
                BattleUnitState.builder().id(aliveHeroId).isHero(true).isAlive(true).build()
        );

        heroService.syncAliveStatusFromBattle(states);

        verify(heroRepository, times(1)).updateAliveStatus(List.of(aliveHeroId), true);
        verify(heroRepository, never()).updateAliveStatus(any(), eq(false));
    }

    @Test
    void syncAliveStatusFromBattle_ignoresNonHeroAndNullIds() {
        UUID deadHeroId = UUID.randomUUID();

        List<BattleUnitState> states = List.of(
                BattleUnitState.builder().id(null).isHero(true).isAlive(true).build(),
                BattleUnitState.builder().id(UUID.randomUUID()).isHero(false).isAlive(false).build(),
                BattleUnitState.builder().id(deadHeroId).isHero(true).isAlive(false).build()
        );

        heroService.syncAliveStatusFromBattle(states);

        verify(heroRepository).updateAliveStatus(List.of(deadHeroId), false);
        verify(heroRepository, never()).updateAliveStatus(any(), eq(true));
    }
}
