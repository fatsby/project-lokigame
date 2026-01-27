package com.theliems.lokigame.repository.inventory;

import com.theliems.lokigame.model.entity.inventory.InventoryItem;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.enums.ItemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryItemRepository extends JpaRepository<InventoryItem, UUID> {
    List<InventoryItem> findByOwner_PlayerId(UUID ownerId);

    List<InventoryItem> findByOwner_PlayerIdAndType(UUID ownerId, ItemType type);

    Optional<InventoryItem> findByOwner_PlayerIdAndItemId(UUID ownerId, String itemId);

    boolean existsByOwner_PlayerIdAndItemId(UUID ownerId, String itemId);

    /**
     * Batch fetch items by their IDs.
     * Used for efficient equipment loading across multiple heroes.
     */
    List<InventoryItem> findAllByIdIn(Collection<UUID> ids);

    List<InventoryItem> findByOwnerAndType(Player player, ItemType itemType);
}
