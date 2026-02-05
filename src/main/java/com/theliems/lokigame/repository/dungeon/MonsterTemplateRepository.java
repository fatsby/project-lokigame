package com.theliems.lokigame.repository.dungeon;

import com.theliems.lokigame.model.entity.dungeon.MonsterTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for MonsterTemplate entities.
 */
@Repository
public interface MonsterTemplateRepository extends JpaRepository<MonsterTemplate, UUID> {
}
