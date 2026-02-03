package com.theliems.lokigame.infrastructure.config;

import com.theliems.lokigame.model.entity.dungeon.DropTable;
import com.theliems.lokigame.model.entity.dungeon.Dungeon;
import com.theliems.lokigame.model.entity.dungeon.Monster;
import com.theliems.lokigame.model.entity.hero.HeroClass;
import com.theliems.lokigame.model.entity.hero.Origin;
import com.theliems.lokigame.model.entity.hero.World;
import com.theliems.lokigame.model.entity.name.Name;
import com.theliems.lokigame.model.enums.NameType;
import com.theliems.lokigame.model.enums.StatType;
import com.theliems.lokigame.repository.dungeon.DungeonRepository;
import com.theliems.lokigame.repository.hero.HeroClassRepository;
import com.theliems.lokigame.repository.hero.OriginRepository;
import com.theliems.lokigame.repository.hero.WorldRepository;
import com.theliems.lokigame.repository.system.NameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GameDataInitialize implements CommandLineRunner {

        private final HeroClassRepository heroClassRepository;
        private final OriginRepository originRepository;
        private final WorldRepository worldRepository;
        private final DungeonRepository dungeonRepository;
        private final NameRepository nameRepository;

        @Transactional
        public void initializeGameData() {
                if (heroClassRepository.count() == 0) {
                        initializeHeroClasses();
                }
                if (originRepository.count() == 0) {
                        initializeOrigins();
                }
                if (worldRepository.count() == 0) {
                        initializeWorlds();
                }
                if (dungeonRepository.count() == 0) {
                        initializeDungeons();
                }
                if (nameRepository.count() == 0) {
                        initializeNames();
                }
                log.info("Game data initialization completed");
        }

        private void initializeHeroClasses() {
                // Warrior
                Map<StatType, Double> warriorBaseStats = new HashMap<>();
                warriorBaseStats.put(StatType.HP, 150.0);
                warriorBaseStats.put(StatType.ATK, 30.0);
                warriorBaseStats.put(StatType.DEF, 20.0);
                warriorBaseStats.put(StatType.SPEED, 40.0);
                warriorBaseStats.put(StatType.CRIT_RATE, 0.05);
                warriorBaseStats.put(StatType.CRIT_DAMAGE, 1.5);

                Map<StatType, Double> warriorModifiers = new HashMap<>();
                warriorModifiers.put(StatType.DEF, 0.2); // +20% DEF
                warriorModifiers.put(StatType.HP, 0.15); // +15% HP

                // Warrior stat growth per level (tank-focused)
                Map<StatType, Double> warriorStatGrowth = new HashMap<>();
                warriorStatGrowth.put(StatType.HP, 8.0);
                warriorStatGrowth.put(StatType.ATK, 2.0);
                warriorStatGrowth.put(StatType.DEF, 3.0);
                warriorStatGrowth.put(StatType.SPEED, 0.5);
                warriorStatGrowth.put(StatType.CRIT_RATE, 0.001);
                warriorStatGrowth.put(StatType.CRIT_DAMAGE, 0.02);

                HeroClass warrior = HeroClass.builder()
                                .name("Warrior")
                                .description("A melee fighter with high defense and health")
                                .baseStats(warriorBaseStats)
                                .statModifiers(warriorModifiers)
                                .statGrowthPerLevel(warriorStatGrowth)
                                .build();
                heroClassRepository.save(warrior);

                // Mage
                Map<StatType, Double> mageBaseStats = new HashMap<>();
                mageBaseStats.put(StatType.HP, 80.0);
                mageBaseStats.put(StatType.ATK, 50.0);
                mageBaseStats.put(StatType.DEF, 10.0);
                mageBaseStats.put(StatType.SPEED, 60.0);
                mageBaseStats.put(StatType.CRIT_RATE, 0.15);
                mageBaseStats.put(StatType.CRIT_DAMAGE, 2.0);

                Map<StatType, Double> mageModifiers = new HashMap<>();
                mageModifiers.put(StatType.ATK, 0.3); // +30% ATK
                mageModifiers.put(StatType.CRIT_RATE, 0.1); // +10% CRIT_RATE

                // Mage stat growth per level (damage-focused)
                Map<StatType, Double> mageStatGrowth = new HashMap<>();
                mageStatGrowth.put(StatType.HP, 3.0);
                mageStatGrowth.put(StatType.ATK, 5.0);
                mageStatGrowth.put(StatType.DEF, 0.5);
                mageStatGrowth.put(StatType.SPEED, 1.0);
                mageStatGrowth.put(StatType.CRIT_RATE, 0.003);
                mageStatGrowth.put(StatType.CRIT_DAMAGE, 0.05);

                HeroClass mage = HeroClass.builder()
                                .name("Mage")
                                .description("A spellcaster with high attack and critical chance")
                                .baseStats(mageBaseStats)
                                .statModifiers(mageModifiers)
                                .statGrowthPerLevel(mageStatGrowth)
                                .build();
                heroClassRepository.save(mage);

                // Rogue
                Map<StatType, Double> rogueBaseStats = new HashMap<>();
                rogueBaseStats.put(StatType.HP, 100.0);
                rogueBaseStats.put(StatType.ATK, 40.0);
                rogueBaseStats.put(StatType.DEF, 15.0);
                rogueBaseStats.put(StatType.SPEED, 80.0);
                rogueBaseStats.put(StatType.CRIT_RATE, 0.25);
                rogueBaseStats.put(StatType.CRIT_DAMAGE, 2.5);

                Map<StatType, Double> rogueModifiers = new HashMap<>();
                rogueModifiers.put(StatType.SPEED, 0.25); // +25% SPEED
                rogueModifiers.put(StatType.CRIT_RATE, 0.2); // +20% CRIT_RATE

                // Rogue stat growth per level (crit-focused)
                Map<StatType, Double> rogueStatGrowth = new HashMap<>();
                rogueStatGrowth.put(StatType.HP, 4.0);
                rogueStatGrowth.put(StatType.ATK, 3.5);
                rogueStatGrowth.put(StatType.DEF, 1.0);
                rogueStatGrowth.put(StatType.SPEED, 2.0);
                rogueStatGrowth.put(StatType.CRIT_RATE, 0.005);
                rogueStatGrowth.put(StatType.CRIT_DAMAGE, 0.08);

                HeroClass rogue = HeroClass.builder()
                                .name("Rogue")
                                .description("A fast assassin with high critical damage")
                                .baseStats(rogueBaseStats)
                                .statModifiers(rogueModifiers)
                                .statGrowthPerLevel(rogueStatGrowth)
                                .build();
                heroClassRepository.save(rogue);

                log.info("Initialized {} hero classes", heroClassRepository.count());
        }

        private void initializeOrigins() {
                // Human
                Map<StatType, Double> humanModifiers = new HashMap<>();
                humanModifiers.put(StatType.HP, 0.1); // +10% HP
                humanModifiers.put(StatType.ATK, 0.05); // +5% ATK

                Origin human = Origin.builder()
                                .name("Human")
                                .description("Balanced race with moderate bonuses")
                                .statModifiers(humanModifiers)
                                .build();
                originRepository.save(human);

                // Elf
                Map<StatType, Double> elfModifiers = new HashMap<>();
                elfModifiers.put(StatType.SPEED, 0.2); // +20% SPEED
                elfModifiers.put(StatType.CRIT_RATE, 0.1); // +10% CRIT_RATE

                Origin elf = Origin.builder()
                                .name("Elf")
                                .description("Swift and agile race")
                                .statModifiers(elfModifiers)
                                .build();
                originRepository.save(elf);

                // Dwarf
                Map<StatType, Double> dwarfModifiers = new HashMap<>();
                dwarfModifiers.put(StatType.DEF, 0.25); // +25% DEF
                dwarfModifiers.put(StatType.HP, 0.15); // +15% HP

                Origin dwarf = Origin.builder()
                                .name("Dwarf")
                                .description("Sturdy and defensive race")
                                .statModifiers(dwarfModifiers)
                                .build();
                originRepository.save(dwarf);

                log.info("Initialized {} origins", originRepository.count());
        }

        private void initializeWorlds() {
                World world1 = World.builder()
                                .name("Aetheria")
                                .description("A mystical realm of magic and wonder")
                                .rarityWeight(50.0)
                                .statMultiplier(1.0)
                                .dungeonDifficultyMod(1.0)
                                .build();
                worldRepository.save(world1);

                World world2 = World.builder()
                                .name("Shadowlands")
                                .description("A dark realm of chaos and danger")
                                .rarityWeight(30.0)
                                .statMultiplier(1.2)
                                .dungeonDifficultyMod(1.2)
                                .build();
                worldRepository.save(world2);

                World world3 = World.builder()
                                .name("Celestia")
                                .description("A heavenly realm of light and purity")
                                .rarityWeight(20.0)
                                .statMultiplier(1.5)
                                .dungeonDifficultyMod(1.5)
                                .build();
                worldRepository.save(world3);

                log.info("Initialized {} worlds", worldRepository.count());
        }

        private void initializeDungeons() {
                // Dungeon 1: Goblin Cave
                Map<StatType, Double> goblinStats = new HashMap<>();
                goblinStats.put(StatType.HP, 2000.0);
                goblinStats.put(StatType.ATK, 150.0);
                goblinStats.put(StatType.DEF, 100.0);
                goblinStats.put(StatType.SPEED, 150.0);
                goblinStats.put(StatType.CRIT_RATE, 0.05);
                goblinStats.put(StatType.CRIT_DAMAGE, 1.5);

                Monster goblin = Monster.builder()
                                .name("Goblin")
                                .description("A small but dangerous creature")
                                .level(1)
                                .stats(goblinStats)
                                .build();

                Dungeon goblinCave = Dungeon.builder()
                                .name("Goblin Cave")
                                .description("A dark cave filled with goblins")
                                .level(1)
                                .build();
                goblin.setDungeon(goblinCave); // Set bidirectional relationship first
                goblinCave.getMonsters().add(goblin);

                DropTable goblinDropTable = DropTable.builder()
                                .dungeon(goblinCave)
                                .baseGold(100L)
                                .goldMultiplier(1.0)
                                .equipmentDropChance(0.3)
                                .materialDropChance(0.5)
                                .baseXp(50L) // Easy dungeon = low XP
                                .xpMultiplier(1.0)
                                .build();
                goblinCave.setDropTable(goblinDropTable);

                dungeonRepository.save(goblinCave);

                // Dungeon 2: Orc Stronghold
                Map<StatType, Double> orcStats = new HashMap<>();
                orcStats.put(StatType.HP, 5000.0);
                orcStats.put(StatType.ATK, 600.0);
                orcStats.put(StatType.DEF, 100.0);
                orcStats.put(StatType.SPEED, 150.0);
                orcStats.put(StatType.CRIT_RATE, 0.1);
                orcStats.put(StatType.CRIT_DAMAGE, 1.8);

                Monster orc = Monster.builder()
                                .name("Orc Warrior")
                                .description("A powerful orc warrior")
                                .level(5)
                                .stats(orcStats)
                                .build();

                Dungeon orcStronghold = Dungeon.builder()
                                .name("Orc Stronghold")
                                .description("A fortified orc encampment")
                                .level(5)
                                .build();
                orc.setDungeon(orcStronghold); // Set bidirectional relationship first
                orcStronghold.getMonsters().add(orc);

                DropTable orcDropTable = DropTable.builder()
                                .dungeon(orcStronghold)
                                .baseGold(500L)
                                .goldMultiplier(1.5)
                                .equipmentDropChance(0.5)
                                .materialDropChance(0.7)
                                .baseXp(150L) // Harder dungeon = more XP
                                .xpMultiplier(1.5)
                                .build();
                orcStronghold.setDropTable(orcDropTable);

                dungeonRepository.save(orcStronghold);

                log.info("Initialized {} dungeons", dungeonRepository.count());
        }

        private void initializeNames() {
                // Female Names
                String[] femaleNames = { "Aria", "Luna", "Zara", "Nova", "Ivy" };
                for (String name : femaleNames) {
                        nameRepository.save(Name.builder().name(name).type(NameType.FEMALE_HERO_NAME).build());
                }

                // Male Names
                String[] maleNames = { "Kael", "Thorin", "Drake", "Rex", "Orion" };
                for (String name : maleNames) {
                        nameRepository.save(Name.builder().name(name).type(NameType.MALE_HERO_NAME).build());
                }

                // Last Names
                String[] lastNames = { "Thatcher", "Blackwood", "Beaumont", "Sterling",
                                "Hawthorne", "Garrick", "Barlow", "Miller",
                                "Valerius", "Crowe", "Hardy", "Vance", "Barlow", "Mordecai", "Pendleton", "Davenport",
                                "Ridley",
                                "Stallard", "Granger" };
                for (String name : lastNames) {
                        nameRepository.save(Name.builder().name(name).type(NameType.HERO_LASTNAME).build());
                }

                // Godsent Custom Equipment Names
                String[] godsentEquipmentNames = { "King Arthur's", "Asgardian Glory", "Hale's Own" };
                for (String name : godsentEquipmentNames) {
                        nameRepository.save(Name.builder().name(name).type(NameType.EQUIPMENT_GODSENT).build());
                }

                String[] equipmentNames = { "Cursed", "Crooked", "Hallowed", "Seraphic", "Primordial",
                                "Malevolent", "Abyssal", "Blighted",
                                "Ethereal", "Sanctified" };
                for (String name : equipmentNames) {
                        nameRepository.save(Name.builder().name(name).type(NameType.EQUIPMENT).build());
                }

                log.info("Initialized {} names", nameRepository.count());
        }

        @Override
        public void run(String... args) throws Exception {
                initializeGameData();
        }
}
