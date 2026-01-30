package com.theliems.lokigame.service.battle;

import com.theliems.lokigame.engine.BattleEngine;
import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.entity.dungeon.Dungeon;
import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.repository.dungeon.DungeonRepository;

import com.theliems.lokigame.service.hero.HeroService;
import com.theliems.lokigame.service.leveling.LevelingService;
import com.theliems.lokigame.service.leveling.XpCalculatorService;
import com.theliems.lokigame.model.dto.leveling.LevelUpResult;
import com.theliems.lokigame.model.dto.battle.BattleSimulateResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BattleService {

        private final BattleEngine battleEngine;
        private final HeroService heroService;
        private final DungeonRepository dungeonRepository;
        private final ExceptionFactory exceptionFactory;
        private final com.theliems.lokigame.mapper.DungeonMapper dungeonMapper;
        private final XpCalculatorService xpCalculatorService;
        private final LevelingService levelingService;

        @Transactional
        public BattleSimulateResponse simulateBattle(List<UUID> heroIds,
                        UUID dungeonId) {
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

                // Award XP on victory
                boolean victory = "HEROES".equals(result.getWinner());
                List<LevelUpResult> levelUpResults = new ArrayList<>();
                long xpReward = 0;

                if (victory && dungeon.getDropTable() != null) {
                        xpReward = xpCalculatorService.calculateBattleXp(dungeon.getDropTable(), true);
//                        long xpPerHero = xpReward / heroes.size();

                        for (Hero hero : heroes) {
                                LevelUpResult levelResult = levelingService.addExperience(hero, xpReward);
                                levelUpResults.add(levelResult);
                        }

                        log.info("Awarded {} XP to {} heroes for dungeon {} victory",
                                        xpReward, heroes.size(), dungeon.getName());
                }

                // Map to DTO
                List<BattleSimulateResponse.BattleLogEntry> logs = result
                                .getLogs()
                                .stream()
                                .map(logEntry -> BattleSimulateResponse.BattleLogEntry
                                                .builder()
                                                .turn(logEntry.getTurn())
                                                .message(logEntry.getMessage())
                                                .build())
                                .collect(Collectors.toList());

                List<BattleSimulateResponse.BattleUnitState> heroStates = result
                                .getHeroUnits() != null ? result.getHeroUnits().stream()
                                                .map(u -> BattleSimulateResponse.BattleUnitState
                                                                .builder()
                                                                .id(u.getId())
                                                                .name(u.getName())
                                                                .maxHp(u.getMaxHp())
                                                                .currentHp(u.getCurrentHp())
                                                                .isHero(u.isHero())
                                                                .build())
                                                .collect(Collectors.toList()) : null;

                List<BattleSimulateResponse.BattleUnitState> monsterStates = result
                                .getMonsterUnits() != null ? result.getMonsterUnits().stream()
                                                .map(u -> BattleSimulateResponse.BattleUnitState
                                                                .builder()
                                                                .id(u.getId())
                                                                .name(u.getName())
                                                                .maxHp(u.getMaxHp())
                                                                .currentHp(u.getCurrentHp())
                                                                .isHero(u.isHero())
                                                                .build())
                                                .collect(Collectors.toList()) : null;

                return BattleSimulateResponse.builder()
                                .winner(result.getWinner())
                                .turns(result.getTurns())
                                .logs(logs)
                                .heroes(heroStates)
                                .monsters(monsterStates)
                                .dungeon(dungeonMapper.toDto(dungeon))
                                .xpAwarded(xpReward)
                                .levelUpResults(levelUpResults)
                                .build();
        }
}
