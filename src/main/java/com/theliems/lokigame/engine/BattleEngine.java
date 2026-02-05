package com.theliems.lokigame.engine;

import com.theliems.lokigame.model.entity.hero.Hero;
import com.theliems.lokigame.model.entity.hero.HeroStats;
import com.theliems.lokigame.model.entity.dungeon.Monster;
import com.theliems.lokigame.model.enums.StatType;
import com.theliems.lokigame.model.dto.battle.BattleLogEntry;
import com.theliems.lokigame.model.dto.battle.BattleUnitState;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class BattleEngine {

    private static final int MAX_TURNS = 100; // Prevent infinite loops

    /**
     * Simulates a turn-based battle between a team of heroes and monsters.
     *
     * @param heroes   List of heroes (team A)
     * @param monsters List of monsters (team B)
     * @return BattleResult with logs, winner, and rewards
     */
    public BattleResult simulateBattle(List<Hero> heroes, List<Monster> monsters) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<BattleLogEntry> logs = new ArrayList<>();

        // Create battle units from heroes and monsters
        List<BattleUnit> heroUnits = heroes.stream()
                .map(this::createBattleUnit)
                .toList();
        List<BattleUnit> monsterUnits = monsters.stream()
                .map(this::createBattleUnit)
                .toList();

        List<BattleUnit> allUnits = new ArrayList<>();
        allUnits.addAll(heroUnits);
        allUnits.addAll(monsterUnits);

        logs.add(BattleLogEntry.builder()
                .turn(0)
                .message("Battle begins! Heroes: " + heroes.size() + ", Monsters: " + monsters.size())
                .actionType("START")
                .heroStates(createSnapshots(heroUnits))
                .monsterStates(createSnapshots(monsterUnits))
                .build());

        int turn = 0;
        while (turn < MAX_TURNS) {
            turn++;

            // Sort units by speed (descending) for turn order
            allUnits.sort((a, b) -> Double.compare(b.getSpeed(), a.getSpeed()));

            // Process each unit's turn
            for (BattleUnit unit : allUnits) {
                if (unit.getCurrentHp() <= 0) {
                    continue; // Skip dead units
                }

                // Determine target
                BattleUnit target = selectTarget(unit, heroUnits, monsterUnits, random);
                if (target == null) {
                    continue; // No valid target
                }

                // Calculate damage
                DamageResult damageResult = calculateDamage(unit, target, random);
                target.setCurrentHp(Math.max(0, target.getCurrentHp() - damageResult.damage));

                logs.add(BattleLogEntry.builder()
                        .turn(turn)
                        .message(String.format("%s attacks %s for %.1f damage. %s HP: %.1f/%.1f",
                                unit.getName(), target.getName(), damageResult.damage,
                                target.getName(), target.getCurrentHp(), target.getMaxHp()))
                        .actionType("ATTACK")
                        .attackerId(unit.getId())
                        .targetId(target.getId())
                        .damage(damageResult.damage)
                        .isCritical(damageResult.isCritical)
                        .heroStates(createSnapshots(heroUnits))
                        .monsterStates(createSnapshots(monsterUnits))
                        .build());

                // Check if battle is over
                boolean heroesAlive = heroUnits.stream().anyMatch(u -> u.getCurrentHp() > 0);
                boolean monstersAlive = monsterUnits.stream().anyMatch(u -> u.getCurrentHp() > 0);

                if (!heroesAlive) {
                    logs.add(BattleLogEntry.builder()
                            .turn(turn)
                            .message("Monsters win!")
                            .actionType("WIN_MONSTERS")
                            .heroStates(createSnapshots(heroUnits))
                            .monsterStates(createSnapshots(monsterUnits))
                            .build());
                    return BattleResult.builder()
                            .logs(logs)
                            .winner("MONSTERS")
                            .turns(turn)
                            .heroUnits(heroUnits)
                            .monsterUnits(monsterUnits)
                            .build();
                }

                if (!monstersAlive) {
                    logs.add(BattleLogEntry.builder()
                            .turn(turn)
                            .message("Heroes win!")
                            .actionType("WIN_HEROES")
                            .heroStates(createSnapshots(heroUnits))
                            .monsterStates(createSnapshots(monsterUnits))
                            .build());
                    return BattleResult.builder()
                            .logs(logs)
                            .winner("HEROES")
                            .turns(turn)
                            .heroUnits(heroUnits)
                            .monsterUnits(monsterUnits)
                            .build();
                }
            }
        }

        logs.add(BattleLogEntry.builder()
                .turn(turn)
                .message("Battle timeout - Draw")
                .actionType("DRAW")
                .heroStates(createSnapshots(heroUnits))
                .monsterStates(createSnapshots(monsterUnits))
                .build());
        return BattleResult.builder()
                .logs(logs)
                .winner("DRAW")
                .turns(turn)
                .heroUnits(heroUnits)
                .monsterUnits(monsterUnits)
                .build();
    }

    private List<BattleUnitState> createSnapshots(List<BattleUnit> units) {
        return units.stream()
                .map(u -> BattleUnitState.builder()
                        .id(u.getId())
                        .name(u.getName())
                        .maxHp(u.getMaxHp())
                        .currentHp(u.getCurrentHp())
                        .isHero(u.isHero())
                        .isAlive(u.getCurrentHp() > 0)
                        .build())
                .toList();
    }

    private BattleUnit createBattleUnit(Hero hero) {
        Map<StatType, Double> stats = new HashMap<>();
        for (HeroStats heroStat : hero.getStats()) {
            stats.put(heroStat.getStatType(), heroStat.getFinalValue());
        }

        return BattleUnit.builder()
                .id(hero.getHeroId())
                .name(hero.getFirstName() + " " + hero.getLastName())
                .maxHp(stats.getOrDefault(StatType.HP, 100.0))
                .currentHp(stats.getOrDefault(StatType.HP, 100.0))
                .atk(stats.getOrDefault(StatType.ATK, 20.0))
                .def(stats.getOrDefault(StatType.DEF, 10.0))
                .speed(stats.getOrDefault(StatType.SPEED, 50.0))
                .critRate(stats.getOrDefault(StatType.CRIT_RATE, 0.05))
                .critDamage(stats.getOrDefault(StatType.CRIT_DAMAGE, 1.5))
                .isHero(true)
                .build();
    }

    private BattleUnit createBattleUnit(Monster monster) {
        return BattleUnit.builder()
                .id(monster.getId())
                .name(monster.getName())
                .maxHp(monster.getStats().getOrDefault(StatType.HP, 100.0))
                .currentHp(monster.getStats().getOrDefault(StatType.HP, 100.0))
                .atk(monster.getStats().getOrDefault(StatType.ATK, 20.0))
                .def(monster.getStats().getOrDefault(StatType.DEF, 10.0))
                .speed(monster.getStats().getOrDefault(StatType.SPEED, 50.0))
                .critRate(monster.getStats().getOrDefault(StatType.CRIT_RATE, 0.05))
                .critDamage(monster.getStats().getOrDefault(StatType.CRIT_DAMAGE, 1.5))
                .isHero(false)
                .build();
    }

    private BattleUnit selectTarget(BattleUnit attacker, List<BattleUnit> heroUnits, List<BattleUnit> monsterUnits,
            ThreadLocalRandom random) {
        List<BattleUnit> targets;
        if (attacker.isHero()) {
            targets = monsterUnits.stream()
                    .filter(u -> u.getCurrentHp() > 0)
                    .toList();
        } else {
            targets = heroUnits.stream()
                    .filter(u -> u.getCurrentHp() > 0)
                    .toList();
        }

        if (targets.isEmpty()) {
            return null;
        }

        // Simple target selection: random alive target
        return targets.get(random.nextInt(targets.size()));
    }

    private DamageResult calculateDamage(BattleUnit attacker, BattleUnit defender, ThreadLocalRandom random) {
        // Base damage formula: ATK * skillMultiplier - DEF
        double skillMultiplier = 1.0; // Can be enhanced with skills later
        double baseDamage = attacker.getAtk() * skillMultiplier - defender.getDef();
        baseDamage = Math.max(1.0, baseDamage); // Minimum 1 damage

        boolean isCritical = false;
        // Check for crit
        if (random.nextDouble() < attacker.getCritRate()) {
            baseDamage *= attacker.getCritDamage();
            isCritical = true;
            log.debug("Critical hit! {} crits {} for {} damage", attacker.getName(), defender.getName(), baseDamage);
        }

        return new DamageResult(baseDamage, isCritical);
    }

    @Data
    @Builder
    public static class BattleUnit {
        private UUID id;
        private String name;
        private double maxHp;
        private double currentHp;
        private double atk;
        private double def;
        private double speed;
        private double critRate;
        private double critDamage;
        private boolean isHero;
    }

    @Data
    @Builder
    public static class BattleResult {
        private List<BattleLogEntry> logs;
        private String winner; // "HEROES", "MONSTERS", or "DRAW"
        private int turns;
        private List<BattleUnit> heroUnits;
        private List<BattleUnit> monsterUnits;
    }

    private record DamageResult(double damage, boolean isCritical) {
    }
}
