package com.theliems.lokigame.service.dungeon;

import com.theliems.lokigame.infrastructure.constants.DungeonConstants;
import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.entity.dungeon.Dungeon;
import com.theliems.lokigame.model.entity.dungeon.DropTable;
import com.theliems.lokigame.model.entity.dungeon.DungeonSeed;
import com.theliems.lokigame.model.entity.dungeon.Monster;
import com.theliems.lokigame.model.entity.dungeon.MonsterTemplate;
import com.theliems.lokigame.model.entity.hero.World;
import com.theliems.lokigame.repository.dungeon.MonsterTemplateRepository;
import com.theliems.lokigame.repository.hero.WorldRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Service responsible for procedurally generating dungeons using seeds.
 * Single Responsibility: Dungeon assembly and orchestration.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DungeonGeneratorService {

    private final MonsterTemplateRepository monsterTemplateRepository;
    private final WorldRepository worldRepository;
    private final MonsterScalingService monsterScalingService;
    private final DropTableCalculatorService dropTableCalculatorService;
    private final ExceptionFactory exceptionFactory;

    /**
     * Generate a dungeon from a DungeonSeed.
     * Using the same seed will always produce the same dungeon composition.
     * 
     * @param dungeonSeed The seed containing level, world, and seed value
     * @return A procedurally generated Dungeon
     */
    public Dungeon generateFromSeed(DungeonSeed dungeonSeed) {
        World world = worldRepository.findById(dungeonSeed.getWorldId())
                .orElseThrow(() -> exceptionFactory.resourceNotFound("World", dungeonSeed.getWorldId()));

        return generateDungeon(
                dungeonSeed.getDungeonLevel(),
                world,
                dungeonSeed.getSeed());
    }

    /**
     * Generate a dungeon at a specific level in a world using a seed.
     * 
     * @param level   The dungeon level
     * @param worldId The world's ID
     * @param seed    The seed for deterministic generation
     * @return A procedurally generated Dungeon
     */
    public Dungeon generateDungeon(int level, UUID worldId, long seed) {
        World world = worldRepository.findById(worldId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("World", worldId));

        return generateDungeon(level, world, seed);
    }

    /**
     * Internal method to generate a dungeon.
     */
    private Dungeon generateDungeon(int level, World world, long seed) {
        Random random = new Random(seed);
        double worldDifficultyMod = world.getDungeonDifficultyMod();

        // Get all available monster templates
        List<MonsterTemplate> allTemplates = monsterTemplateRepository.findAll();
        if (allTemplates.isEmpty()) {
            throw exceptionFactory.validationError("No monster templates available for dungeon generation");
        }

        // Select random subset of monsters
        List<MonsterTemplate> selectedTemplates = selectRandomMonsters(allTemplates, random);

        // Scale selected monsters to dungeon level
        List<Monster> scaledMonsters = monsterScalingService.scaleAllToLevel(
                selectedTemplates, level, worldDifficultyMod);

        // Calculate drop table
        DropTable dropTable = dropTableCalculatorService.calculate(level, worldDifficultyMod);

        // Generate dungeon name
        String dungeonName = generateDungeonName(level, world.getName(), random);

        log.info("Generated dungeon '{}' at level {} in world '{}' with {} monsters (seed: {})",
                dungeonName, level, world.getName(), scaledMonsters.size(), seed);

        return Dungeon.builder()
                .id(UUID.randomUUID())
                .name(dungeonName)
                .description(String.format("A level %d dungeon in %s", level, world.getName()))
                .level(level)
                .worldId(world.getWorldId())
                .seed(seed)
                .monsters(scaledMonsters)
                .dropTable(dropTable)
                .build();
    }

    /**
     * Select a random subset of monster templates for the dungeon.
     */
    private List<MonsterTemplate> selectRandomMonsters(List<MonsterTemplate> allTemplates, Random random) {
        int minMonsters = DungeonConstants.MIN_MONSTERS_PER_DUNGEON;
        int maxMonsters = DungeonConstants.MAX_MONSTERS_PER_DUNGEON;

        // Determine number of monsters (between min and max, but not more than
        // available)
        int availableCount = allTemplates.size();
        int maxPossible = Math.min(maxMonsters, availableCount);
        int monsterCount = minMonsters + random.nextInt(Math.max(1, maxPossible - minMonsters + 1));

        // Shuffle and select
        List<MonsterTemplate> shuffled = new ArrayList<>(allTemplates);
        for (int i = shuffled.size() - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            MonsterTemplate temp = shuffled.get(i);
            shuffled.set(i, shuffled.get(j));
            shuffled.set(j, temp);
        }

        return shuffled.subList(0, Math.min(monsterCount, shuffled.size()));
    }

    /**
     * Generate a procedural name for the dungeon.
     */
    private String generateDungeonName(int level, String worldName, Random random) {
        String[] prefixes = { "Dark", "Ancient", "Cursed", "Forgotten", "Haunted", "Shadowy", "Mystic", "Infernal" };
        String[] types = { "Cave", "Dungeon", "Crypt", "Cavern", "Lair", "Vault", "Labyrinth", "Depths" };

        String prefix = prefixes[random.nextInt(prefixes.length)];
        String type = types[random.nextInt(types.length)];

        return String.format("%s %s (Level %d)", prefix, type, level);
    }
}
