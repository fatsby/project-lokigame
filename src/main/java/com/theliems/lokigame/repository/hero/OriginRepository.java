package com.theliems.lokigame.repository.hero;

import com.theliems.lokigame.model.entity.hero.Origin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OriginRepository extends JpaRepository<Origin, UUID> {
}
