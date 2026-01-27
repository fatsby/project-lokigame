package com.theliems.lokigame.config;

import com.theliems.lokigame.model.entity.dungeon.DropTable;
import com.theliems.lokigame.model.entity.dungeon.Dungeon;
import com.theliems.lokigame.model.entity.hero.HeroClass;
import com.theliems.lokigame.model.entity.hero.Origin;
import com.theliems.lokigame.model.entity.hero.World;
import com.theliems.lokigame.model.enums.StatType;
import com.theliems.lokigame.repository.dungeon.DropTableRepository;
import com.theliems.lokigame.repository.dungeon.DungeonRepository;
import com.theliems.lokigame.repository.hero.HeroClassRepository;
import com.theliems.lokigame.repository.hero.OriginRepository;
import com.theliems.lokigame.repository.hero.WorldRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameInitialize2 implements CommandLineRunner {

    private final HeroClassRepository heroClassRepository;
    private final OriginRepository originRepository;
    private final WorldRepository worldRepository;
    private final DungeonRepository dungeonRepository;
    private final DropTableRepository dropTableRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting Enhanced Game Initialization Phase 2...");

        if (heroClassRepository.count() == 0) {
            initClasses();
        }
        if (originRepository.count() == 0) {
            initOrigins();
        }
        if (worldRepository.count() == 0) {
            initWorlds();
        }
        if (dungeonRepository.count() == 0) {
            initDungeons();
        }

        log.info("Enhanced Game Initialization Complete.");
    }

    private void initClasses() {
        log.info("Initializing Hero Classes with Lore...");

        saveClass("Void Walker", "Assassin of the empty spaces.",
                Map.of(StatType.ATK, 30.0, StatType.SPEED, 70.0, StatType.CRIT_RATE, 0.20),
                Map.of(StatType.HP, -0.2)); // Glass cannon

        saveClass("Solar Paladin", "Warrior fueled by the dying sun.",
                Map.of(StatType.HP, 150.0, StatType.DEF, 20.0),
                Map.of(StatType.SPEED, -0.1));

        saveClass("Cyber Mage", "Weaver of digital arcane threads.",
                Map.of(StatType.ATK, 40.0, StatType.CRIT_DAMAGE, 2.0),
                Map.of(StatType.DEF, -0.3));

        saveClass("Bio-Titan", "Genetically engineered monstrosity.",
                Map.of(StatType.HP, 300.0, StatType.ATK, 10.0),
                Map.of(StatType.SPEED, -0.5, StatType.DEF, 0.2));

        saveClass("Chrono Thief", "Steals time itself from enemies.",
                Map.of(StatType.SPEED, 100.0, StatType.CRIT_RATE, 0.15),
                Map.of(StatType.HP, -0.1));
    }

    private void saveClass(String name, String desc, Map<StatType, Double> base, Map<StatType, Double> mods) {
        HeroClass hc = HeroClass.builder()
                .name(name)
                .description(desc)
                .baseStats(base)
                .statModifiers(mods)
                .build();
        heroClassRepository.save(hc);
    }

    private void initOrigins() {
        log.info("Initializing Origins...");

        saveOrigin("Noble Lineage", "Descended from fallen kings.", Map.of(StatType.ATK, 0.1, StatType.DEF, 0.1));
        saveOrigin("Street Rat", "Raised in the neon slums.", Map.of(StatType.SPEED, 0.15, StatType.CRIT_RATE, 0.05));
        saveOrigin("Lab Experiment", "Escaped from Sector 7.", Map.of(StatType.HP, 0.2, StatType.DEF, 0.1));
        saveOrigin("Ancient Soul", "Reincarnated from the Old World.", Map.of(StatType.CRIT_DAMAGE, 0.2));
    }

    private void saveOrigin(String name, String desc, Map<StatType, Double> mods) {
        Origin o = Origin.builder()
                .name(name)
                .description(desc)
                .statModifiers(mods)
                .build();
        originRepository.save(o);
    }

    private void initWorlds() {
        log.info("Initializing Worlds...");

        saveWorld("Terra Prime", "The homeworld. Recovering from collapse.", 100, 0.0, 1.0);
        saveWorld("Neon Tokyo", "A cyberpunk metropolis run by AI.", 50, 0.2, 1.5);
        saveWorld("Helheim", "The frozen wastes of the north.", 20, 0.5, 2.0);
        saveWorld("The Void", "A dimension of pure entropy.", 5, 1.0, 3.0); // Rare, double stats
        saveWorld("Valhalla", "The eternal battlefield.", 1, 2.0, 5.0); // Very rare, triple stats
    }

    private void saveWorld(String name, String desc, Integer rarity, Double statMod, Double diffMod) {
        World w = World.builder()
                .name(name)
                .description(desc)
                .rarityWeight(rarity)
                .statMultiplier(statMod)
                .dungeonDifficultyMod(diffMod)
                .build();
        worldRepository.save(w);
    }

    private void initDungeons() {
        log.info("Initializing Dungeons...");

        createDungeon("Rat Cellars", 1, 100L, 1.0, 0.1, 0.05);
        createDungeon("Goblin Outpost", 5, 250L, 1.2, 0.2, 0.1);
        createDungeon("Cyber-Dragon's Lair", 20, 1000L, 2.0, 0.5, 0.3);
        createDungeon("Void Nexus", 50, 5000L, 5.0, 0.8, 0.5);
        createDungeon("Throne of Gods", 99, 100000L, 10.0, 1.0, 1.0); // Guaranteed drops
    }

    private void createDungeon(String name, Integer level, Long baseGold, Double goldMult, Double equipChance,
            Double matChance) {
        DropTable dt = DropTable.builder()
                .baseGold(baseGold)
                .goldMultiplier(goldMult)
                .equipmentDropChance(equipChance)
                .materialDropChance(matChance)
                .build();

        dt = dropTableRepository.save(dt);

        Dungeon d = Dungeon.builder()
                .name(name)
                .level(level)
                .dropTable(dt)
                .build();
        dungeonRepository.save(d);
    }
}
