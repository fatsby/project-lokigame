package com.theliems.lokigame.service.dungeon;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.model.entity.dungeon.DungeonSeed;
import com.theliems.lokigame.model.entity.hero.World;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.repository.dungeon.DungeonSeedRepository;
import com.theliems.lokigame.repository.player.PlayerRepository;
import com.theliems.lokigame.utils.WorldUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Service responsible for managing dungeon seeds and enforcing level gating.
 * Single Responsibility: Seed management and progression validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DungeonSeedService {

    private final DungeonSeedRepository dungeonSeedRepository;
    private final PlayerRepository playerRepository;
    private final ExceptionFactory exceptionFactory;
    private final WorldUtils worldUtils;

    /**
     * Validate if a player can attempt the requested dungeon level.
     * 
     * Level Gating Rules:
     * - Player can only attempt level <= highestClearedLevel + 1
     * 
     * @param playerId       The player's ID
     * @param requestedLevel The level the player wants to attempt
     * @throws RuntimeException if level access is denied
     */
    public void validateLevelAccess(UUID playerId, int requestedLevel) {
        if (requestedLevel < 1) {
            throw exceptionFactory.validationError("Dungeon level must be at least 1");
        }

        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Player", playerId));

        int maxAllowedLevel = player.getHighestClearedLevel() + 1;

        if (requestedLevel > maxAllowedLevel) {
            throw exceptionFactory.validationError(
                    String.format("Cannot access level %d. Highest cleared: %d. Max allowed: %d",
                            requestedLevel, player.getHighestClearedLevel(), maxAllowedLevel));
        }

        log.debug("Player {} validated for level {} (highest cleared: {})",
                playerId, requestedLevel, player.getHighestClearedLevel());
    }

    /**
     * Get or create a seed for a player's dungeon level.
     * 
     * Rules:
     * - If seed exists for this level, return it (no re-seeding)
     * - If no seed exists and level is valid, roll a new world and create new seed
     * 
     * @param playerId The player's ID
     * @param level    The dungeon level
     * @return The existing or newly created DungeonSeed
     */
    @Transactional
    public DungeonSeed getOrCreateSeed(UUID playerId, int level) {
        // 1. Check if seed already exists for this level (regardless of world)
        Optional<DungeonSeed> existingSeed = dungeonSeedRepository
                .findFirstByPlayerIdAndDungeonLevel(playerId, level);

        if (existingSeed.isPresent()) {
            DungeonSeed seed = existingSeed.get();
            log.debug("Returning existing seed for player {} at level {} in world {}",
                    playerId, level, seed.getWorldId());
            return seed;
        }

        // 2. If no seed, validate access and create new one
        validateLevelAccess(playerId, level);

        // Roll a random world
        World world = worldUtils.rollWorld(ThreadLocalRandom.current());
        UUID worldId = world.getWorldId();

        // Create new random seed
        long newSeed = ThreadLocalRandom.current().nextLong();

        DungeonSeed dungeonSeed = DungeonSeed.builder()
                .playerId(playerId)
                .worldId(worldId)
                .dungeonLevel(level)
                .seed(newSeed)
                .cleared(false)
                .build();

        dungeonSeed = dungeonSeedRepository.save(dungeonSeed);
        log.info("Created new seed {} for player {} at level {} in world {} ('{}')",
                newSeed, playerId, level, worldId, world.getName());

        return dungeonSeed;
    }

    /**
     * Mark a dungeon seed as cleared and update player progression.
     * 
     * @param playerId The player's ID
     * @param worldId  The world's ID
     * @param level    The dungeon level that was cleared
     */
    @Transactional
    public void markCleared(UUID playerId, UUID worldId, int level) {
        DungeonSeed seed = dungeonSeedRepository
                .findByPlayerIdAndWorldIdAndDungeonLevel(playerId, worldId, level)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("DungeonSeed",
                        String.format("player=%s, world=%s, level=%d", playerId, worldId, level)));

        seed.setCleared(true);
        dungeonSeedRepository.save(seed);

        // Update player's highest cleared level
        Player player = playerRepository.findById(playerId)
                .orElseThrow(() -> exceptionFactory.resourceNotFound("Player", playerId));

        if (level > player.getHighestClearedLevel()) {
            player.setHighestClearedLevel(level);
            playerRepository.save(player);
            log.info("Player {} progression updated: highestClearedLevel = {}", playerId, level);
        }

        log.info("Dungeon level {} marked as cleared for player {} in world {}", level, playerId, worldId);
    }

    /**
     * Get all cleared seeds for a player in a world (for replay purposes).
     * 
     * @param playerId The player's ID
     * @param worldId  The world's ID
     * @return List of cleared DungeonSeeds
     */
    public List<DungeonSeed> getClearedSeeds(UUID playerId, UUID worldId) {
        return dungeonSeedRepository.findByPlayerIdAndWorldIdAndCleared(playerId, worldId, true);
    }

    /**
     * Get all seeds for a player in a world.
     * 
     * @param playerId The player's ID
     * @param worldId  The world's ID
     * @return List of all DungeonSeeds
     */
    public List<DungeonSeed> getAllSeeds(UUID playerId, UUID worldId) {
        return dungeonSeedRepository.findByPlayerIdAndWorldId(playerId, worldId);
    }
}
