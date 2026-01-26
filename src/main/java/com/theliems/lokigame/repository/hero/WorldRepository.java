package com.theliems.lokigame.repository.hero;

import com.theliems.lokigame.model.entity.hero.World;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WorldRepository extends JpaRepository<World, UUID> {
}
