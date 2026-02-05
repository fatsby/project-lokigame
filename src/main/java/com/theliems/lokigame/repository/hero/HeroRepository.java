package com.theliems.lokigame.repository.hero;

import com.theliems.lokigame.model.entity.hero.Hero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HeroRepository extends JpaRepository<Hero, UUID> {
    List<Hero> findByOwner_PlayerId(UUID ownerId);

    /**
     * Find all heroes for a player with full details for leveling.
     */
    default List<Hero> findByPlayerId(UUID playerId) {
        return findByPlayerIdFull(playerId);
    }

    @Query("""
                select distinct h from Hero h
                join fetch h.heroClass
                join fetch h.origin
                join fetch h.originWorld
                left join fetch h.stats
                where h.owner.playerId = :playerId and h.alive = true
            """)
    List<Hero> findByPlayerIdFull(@Param("playerId") UUID playerId);

    @Query("""
                select distinct h from Hero h
                join fetch h.heroClass
                join fetch h.origin
                join fetch h.originWorld
                left join fetch h.stats
                where h.id = :heroId
            """)
    Optional<Hero> findByIdWithDetails(@Param("heroId") UUID heroId);

    /**
     * Counts how many heroes from the provided list are actually alive.
     * Spring Data JPA automatically derives the SQL from the method name.
     */
    long countByHeroIdInAndAliveTrue(Collection<UUID> heroIds);

    /**
     * Checks if every Hero UUID in the list exists and has alive = true.
     * Returns true only if the number of alive heroes found matches the number of IDs provided.
     */
    default boolean areAllHeroesAlive(Collection<UUID> heroIds) {
        if (heroIds == null || heroIds.isEmpty()) {
            return false;
        }

        return countByHeroIdInAndAliveTrue(heroIds) == heroIds.size();
    }

    @Modifying(clearAutomatically = true)
    @Query("UPDATE Hero h SET h.alive = :status WHERE h.heroId IN :heroIds")
    void updateAliveStatus(@Param("heroIds") Collection<UUID> heroIds, @Param("status") boolean status);
}
