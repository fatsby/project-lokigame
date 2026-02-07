package com.theliems.lokigame.infrastructure.config;

import com.theliems.lokigame.model.entity.dungeon.MonsterTemplate;
import com.theliems.lokigame.model.entity.hero.HeroClass;
import com.theliems.lokigame.model.entity.hero.Origin;
import com.theliems.lokigame.model.entity.hero.World;
import com.theliems.lokigame.model.entity.name.Name;
import com.theliems.lokigame.model.enums.NameType;
import com.theliems.lokigame.model.enums.StatType;
import com.theliems.lokigame.repository.dungeon.MonsterTemplateRepository;
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
        private final MonsterTemplateRepository monsterTemplateRepository;
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
                if (monsterTemplateRepository.count() == 0) {
                        initializeMonsterTemplates();
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
                warriorBaseStats.put(StatType.CRIT_DAMAGE, 1.2);

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
                mageBaseStats.put(StatType.CRIT_DAMAGE, 1.35);

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
                rogueBaseStats.put(StatType.CRIT_DAMAGE, 1.5);

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

        /**
         * Initialize monster templates for procedural dungeon generation.
         * Templates define base stats and growth - actual monsters are scaled at
         * runtime.
         */
        private void initializeMonsterTemplates() {
                // Goblin - Weak but fast
                Map<StatType, Double> goblinBaseStats = new HashMap<>();
                goblinBaseStats.put(StatType.HP, 100.0);
                goblinBaseStats.put(StatType.ATK, 40.0);
                goblinBaseStats.put(StatType.DEF, 5.0);
                goblinBaseStats.put(StatType.SPEED, 80.0);
                goblinBaseStats.put(StatType.CRIT_RATE, 0.1);
                goblinBaseStats.put(StatType.CRIT_DAMAGE, 1.5);

                Map<StatType, Double> goblinGrowth = new HashMap<>();
                goblinGrowth.put(StatType.HP, 35.0);
                goblinGrowth.put(StatType.ATK, 15.0);
                goblinGrowth.put(StatType.DEF, 2.0);
                goblinGrowth.put(StatType.SPEED, 3.0);
                goblinGrowth.put(StatType.CRIT_RATE, 0.005);
                goblinGrowth.put(StatType.CRIT_DAMAGE, 0.02);

                MonsterTemplate goblin = MonsterTemplate.builder()
                                .name("Goblin")
                                .description("A small but dangerous creature")
                                .baseStats(goblinBaseStats)
                                .statGrowthPerLevel(goblinGrowth)
                                .build();
                monsterTemplateRepository.save(goblin);

                // Orc Warrior - Tanky with high attack
                Map<StatType, Double> orcBaseStats = new HashMap<>();
                orcBaseStats.put(StatType.HP, 250.0);
                orcBaseStats.put(StatType.ATK, 75.0);
                orcBaseStats.put(StatType.DEF, 15.0);
                orcBaseStats.put(StatType.SPEED, 40.0);
                orcBaseStats.put(StatType.CRIT_RATE, 0.05);
                orcBaseStats.put(StatType.CRIT_DAMAGE, 1.8);

                Map<StatType, Double> orcGrowth = new HashMap<>();
                orcGrowth.put(StatType.HP, 50.0);
                orcGrowth.put(StatType.ATK, 20.0);
                orcGrowth.put(StatType.DEF, 5.0);
                orcGrowth.put(StatType.SPEED, 1.0);
                orcGrowth.put(StatType.CRIT_RATE, 0.003);
                orcGrowth.put(StatType.CRIT_DAMAGE, 0.03);

                MonsterTemplate orc = MonsterTemplate.builder()
                                .name("Orc Warrior")
                                .description("A powerful orc warrior")
                                .baseStats(orcBaseStats)
                                .statGrowthPerLevel(orcGrowth)
                                .build();
                monsterTemplateRepository.save(orc);

                // Skeleton Archer - Glass cannon
                Map<StatType, Double> skeletonBaseStats = new HashMap<>();
                skeletonBaseStats.put(StatType.HP, 80.0);
                skeletonBaseStats.put(StatType.ATK, 90.0);
                skeletonBaseStats.put(StatType.DEF, 3.0);
                skeletonBaseStats.put(StatType.SPEED, 60.0);
                skeletonBaseStats.put(StatType.CRIT_RATE, 0.2);
                skeletonBaseStats.put(StatType.CRIT_DAMAGE, 2.0);

                Map<StatType, Double> skeletonGrowth = new HashMap<>();
                skeletonGrowth.put(StatType.HP, 20.0);
                skeletonGrowth.put(StatType.ATK, 30.0);
                skeletonGrowth.put(StatType.DEF, 1.0);
                skeletonGrowth.put(StatType.SPEED, 2.0);
                skeletonGrowth.put(StatType.CRIT_RATE, 0.008);
                skeletonGrowth.put(StatType.CRIT_DAMAGE, 0.05);

                MonsterTemplate skeleton = MonsterTemplate.builder()
                                .name("Skeleton Archer")
                                .description("An undead archer with deadly precision")
                                .baseStats(skeletonBaseStats)
                                .statGrowthPerLevel(skeletonGrowth)
                                .build();
                monsterTemplateRepository.save(skeleton);

                // Troll - Very tanky, slow but powerful
                Map<StatType, Double> trollBaseStats = new HashMap<>();
                trollBaseStats.put(StatType.HP, 400.0);
                trollBaseStats.put(StatType.ATK, 65.0);
                trollBaseStats.put(StatType.DEF, 25.0);
                trollBaseStats.put(StatType.SPEED, 20.0);
                trollBaseStats.put(StatType.CRIT_RATE, 0.02);
                trollBaseStats.put(StatType.CRIT_DAMAGE, 1.5);

                Map<StatType, Double> trollGrowth = new HashMap<>();
                trollGrowth.put(StatType.HP, 100.0);
                trollGrowth.put(StatType.ATK, 15.0);
                trollGrowth.put(StatType.DEF, 15.0);
                trollGrowth.put(StatType.SPEED, 0.5);
                trollGrowth.put(StatType.CRIT_RATE, 0.001);
                trollGrowth.put(StatType.CRIT_DAMAGE, 0.01);

                MonsterTemplate troll = MonsterTemplate.builder()
                                .name("Forest Troll")
                                .description("A massive troll with regenerative abilities")
                                .baseStats(trollBaseStats)
                                .statGrowthPerLevel(trollGrowth)
                                .build();
                monsterTemplateRepository.save(troll);

                // Dark Mage - High damage caster
                Map<StatType, Double> mageBaseStats = new HashMap<>();
                mageBaseStats.put(StatType.HP, 120.0);
                mageBaseStats.put(StatType.ATK, 85.0);
                mageBaseStats.put(StatType.DEF, 5.0);
                mageBaseStats.put(StatType.SPEED, 50.0);
                mageBaseStats.put(StatType.CRIT_RATE, 0.15);
                mageBaseStats.put(StatType.CRIT_DAMAGE, 2.2);

                Map<StatType, Double> mageGrowth = new HashMap<>();
                mageGrowth.put(StatType.HP, 30.0);
                mageGrowth.put(StatType.ATK, 25.0);
                mageGrowth.put(StatType.DEF, 1.5);
                mageGrowth.put(StatType.SPEED, 2.0);
                mageGrowth.put(StatType.CRIT_RATE, 0.006);
                mageGrowth.put(StatType.CRIT_DAMAGE, 0.06);

                MonsterTemplate darkMage = MonsterTemplate.builder()
                                .name("Dark Mage")
                                .description("A sinister spellcaster wielding dark magic")
                                .baseStats(mageBaseStats)
                                .statGrowthPerLevel(mageGrowth)
                                .build();
                monsterTemplateRepository.save(darkMage);

                log.info("Initialized {} monster templates", monsterTemplateRepository.count());
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
