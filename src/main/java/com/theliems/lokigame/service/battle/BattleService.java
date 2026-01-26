package com.theliems.lokigame.service.battle;

import com.theliems.lokigame.engine.BattleEngine;
import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.entity.dungeon.Dungeon;
import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.repository.dungeon.DungeonRepository;
import com.theliems.lokigame.repository.hero.HeroRepository;
import com.theliems.lokigame.service.hero.HeroService;
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
public class BattleService {

    private final BattleEngine battleEngine;
    private final HeroRepository heroRepository;
    private final HeroService heroService;
    private final DungeonRepository dungeonRepository;
    private final ExceptionFactory exceptionFactory;

    @Transactional(readOnly = true)
    public BattleEngine.BattleResult simulateBattle(List<UUID> heroIds, UUID dungeonId) {
        // Load heroes
        List<Hero> heroes = heroIds.stream()
                .map(heroId -> heroService.getHeroById(heroId))
                .collect(Collectors.toList());

        if (heroes.isEmpty()) {
            throw exceptionFactory.validationError("At least one hero is required");
        }

        // Load dungeon and monsters
        Dungeon dungeon = dungeonRepository.findById(dungeonId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Dungeon", dungeonId));

        if (dungeon.getMonsters().isEmpty()) {
            throw exceptionFactory.validationError("Dungeon has no monsters");
        }

        // Simulate battle
        BattleEngine.BattleResult result = battleEngine.simulateBattle(heroes, dungeon.getMonsters());

        log.info("Battle simulation completed: Winner={}, Turns={}", result.getWinner(), result.getTurns());

        return result;
    }
}
