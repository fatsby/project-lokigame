package com.theliems.lokigame.repository.inventory;

import com.theliems.lokigame.model.entity.inventory.InventoryItem;
import com.theliems.lokigame.model.entity.player.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Repository for polymorphic InventoryItem queries.
 * For subclass-specific queries, use the dedicated subclass repositories.
 */
@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {

    List<InventoryItem> findByOwner_PlayerId(UUID ownerId);

    List<InventoryItem> findAllByIdIn(Collection<UUID> ids);

    List<InventoryItem> findByOwner(Player player);
}
