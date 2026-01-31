package com.theliems.lokigame.repository.inventory;

import com.theliems.lokigame.model.entity.inventory.EquipmentItem;
import com.theliems.lokigame.model.enums.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for EquipmentItem-specific queries.
 */
@Repository
public interface EquipmentItemRepository extends JpaRepository<EquipmentItem, UUID> {

    List<EquipmentItem> findByOwner_PlayerId(UUID playerId);

    List<EquipmentItem> findByOwner_PlayerIdAndEquipmentType(UUID playerId, EquipmentType type);
}
