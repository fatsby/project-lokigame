package com.theliems.lokigame.repository.hero;

import com.theliems.lokigame.model.entity.hero.HeroClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HeroClassRepository extends JpaRepository<HeroClass, UUID> {
}
