package com.theliems.lokigame.repository.equipment;

import com.theliems.lokigame.model.entity.equipment.Equipment;
import com.theliems.lokigame.model.entity.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {
    List<Equipment> findByOwner(Player owner);
    List<Equipment> findByOwner_PlayerId(UUID playerId);
}
