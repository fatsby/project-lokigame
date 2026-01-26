package com.theliems.lokigame.repository.hero;

import com.theliems.lokigame.model.entity.hero.Hero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HeroRepository extends JpaRepository<Hero, UUID> {
    List<Hero> findByOwner_PlayerId(UUID ownerId);


    @Query("""
                select distinct h from Hero h
                join fetch h.heroClass
                join fetch h.origin
                join fetch h.originWorld
                left join fetch h.stats
                where h.owner.playerId = :playerId
            """)
    List<Hero> findByPlayerIdFull(@Param("playerId") UUID playerId);

}
