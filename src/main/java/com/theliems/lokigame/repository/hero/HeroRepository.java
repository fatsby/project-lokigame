package com.theliems.lokigame.repository.hero;

import com.theliems.lokigame.model.entity.hero.Hero;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HeroRepository extends JpaRepository<Hero, UUID> {
  List<Hero> findByOwnerId(UUID ownerId);

  Optional<Hero> findByOwnerIdAndId(UUID ownerId, UUID id);

  /**
   * Get 10% of Hero stats for a list of heroes by OwnerID. Heroes must be alive
   * and active
   * 
   * @param ownerId
   * @param heroIds
   * @return
   */
  @Query(value = """
      SELECT
          key as stat_name,
          SUM(CAST(value AS numeric) * 0.1) as aggregated_value
      FROM heroes,
           jsonb_each_text(stats)
      WHERE owner_id = :ownerId
        AND id IN (:heroIds)
        AND alive = true
        AND active = true
      GROUP BY key
      """, nativeQuery = true)
  List<Object[]> aggregateHeroStats(
      @Param("ownerId") UUID ownerId,
      @Param("heroIds") List<UUID> heroIds);

  @Modifying
  @Query(value = """
      UPDATE heroes
      SET alive = :alive
      WHERE owner_id = :ownerId
        AND id IN :heroIds
      """, nativeQuery = true)
  Optional<Void> toggleHeroesAlive(@Param("ownerId") UUID ownerId, @Param("heroIds") List<UUID> heroIds,
      @Param("alive") Boolean alive);
}
