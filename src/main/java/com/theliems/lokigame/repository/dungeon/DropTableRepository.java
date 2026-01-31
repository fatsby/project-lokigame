package com.theliems.lokigame.repository.dungeon;

import com.theliems.lokigame.model.entity.dungeon.DropTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DropTableRepository extends JpaRepository<DropTable, UUID> {
}
