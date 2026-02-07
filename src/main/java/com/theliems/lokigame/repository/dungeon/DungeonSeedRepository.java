package com.theliems.lokigame.repository.dungeon;

import com.theliems.lokigame.model.entity.dungeon.DungeonSeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for DungeonSeed entities.
 * Manages dungeon seed history for deterministic regeneration and replay.
 */
@Repository
public interface DungeonSeedRepository extends JpaRepository<DungeonSeed, UUID> {

    /**
     * Find a specific seed for a player's dungeon level in a world.
     */
    Optional<DungeonSeed> findByPlayerIdAndWorldIdAndDungeonLevel(UUID playerId, UUID worldId, Integer dungeonLevel);

    /**
     * Find the first seed for a player's dungeon level, regardless of world.
     * Used to check if a seed already exists for a level.
     */
    Optional<DungeonSeed> findFirstByPlayerIdAndDungeonLevel(UUID playerId, Integer dungeonLevel);

    /**
     * Find all seeds for a player in a world, filtered by cleared status.
     */
    List<DungeonSeed> findByPlayerIdAndWorldIdAndCleared(UUID playerId, UUID worldId, Boolean cleared);

    /**
     * Find all seeds for a player in a world.
     */
    List<DungeonSeed> findByPlayerIdAndWorldId(UUID playerId, UUID worldId);

    /**
     * Find all seeds for a player.
     */
    List<DungeonSeed> findByPlayerId(UUID playerId);
}
