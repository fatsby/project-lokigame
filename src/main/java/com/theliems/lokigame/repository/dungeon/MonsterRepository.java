package com.theliems.lokigame.repository.dungeon;

import com.theliems.lokigame.model.entity.dungeon.Monster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MonsterRepository extends JpaRepository<Monster, UUID> {
}
