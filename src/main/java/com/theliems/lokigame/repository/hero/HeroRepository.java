package com.theliems.lokigame.repository.hero;

import com.theliems.lokigame.model.entity.hero.Hero;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HeroRepository extends JpaRepository<Hero, UUID> {
    List<Hero> findByOwnerId(UUID ownerId);
}
