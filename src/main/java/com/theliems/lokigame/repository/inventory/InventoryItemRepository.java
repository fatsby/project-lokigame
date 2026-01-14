package com.theliems.lokigame.repository.inventory;

import com.theliems.lokigame.model.entity.inventory.InventoryItem;
import com.theliems.lokigame.model.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
    List<InventoryItem> findByOwnerId(UUID ownerId);

    List<InventoryItem> findByOwnerIdAndType(UUID ownerId, ItemType type);

    Optional<InventoryItem> findByOwnerIdAndItemId(UUID ownerId, String itemId);

    boolean existsByOwnerIdAndItemId(UUID ownerId, String itemId);

    /**
     * Batch fetch items by their IDs.
     * Used for efficient equipment loading across multiple heroes.
     */
    List<InventoryItem> findAllByIdIn(Collection<UUID> ids);
}
