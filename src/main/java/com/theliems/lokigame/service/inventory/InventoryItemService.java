package com.theliems.lokigame.service.inventory;

import com.theliems.lokigame.infrastructure.exception.ExceptionFactory;
import com.theliems.lokigame.infrastructure.exception.errorCategories.InventoryError;
import com.theliems.lokigame.model.entity.hero.StatRange;
import com.theliems.lokigame.model.entity.inventory.InventoryItem;
import com.theliems.lokigame.model.entity.inventory.ItemDefinition;
import com.theliems.lokigame.model.enums.ItemTier;
import com.theliems.lokigame.model.enums.ItemType;
import com.theliems.lokigame.repository.inventory.InventoryItemRepository;
import com.theliems.lokigame.service.gameData.registry.ItemRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InventoryItemService {

    private final InventoryItemRepository inventoryItemRepository;
    private final ExceptionFactory exceptionFactory;
    private final ItemRegistry itemRegistry;

    // --- Creation ---

    /**
     * Creates a single inventory item.
     * If metadata is null, it generates RNG stats based on Tier and Type.
     */
    @Transactional
    public InventoryItem createItem(UUID ownerId, String itemId, ItemType type, ItemTier tier, Map<String, Object> metadata) {
        if (ownerId == null || itemId == null || type == null || tier == null) {
            throw exceptionFactory.createCustomException(InventoryError.INVALID_ITEM_CREATION);
        }

        if (metadata == null) {
            metadata = generateStats(itemId, tier);
        }

        InventoryItem item = InventoryItem.builder()
                .ownerId(ownerId)
                .itemId(itemId)
                .type(type)
                .tier(tier)
                .metadata(metadata)
                .build();

        return inventoryItemRepository.save(item);
    }

    /**
     * Creates a default/base item (NORMAL tier, NO stats).
     * Useful for Hero Generation (starter gear) or basic shop purchases.
     */
    @Transactional
    public InventoryItem createDefaultItem(UUID ownerId, String itemId, ItemType type) {
        // Pass empty map to bypass RNG generation -> Pure cosmetic / No stats
        return createItem(ownerId, itemId, type, ItemTier.NORMAL, new HashMap<>());
    }

    /**
     * Batch creation of items.
     * Highly recommended for "Open 10 Chests" or "Starter Pack" logic to reduce DB round-trips.
     */
    @Transactional
    public List<InventoryItem> createItems(UUID ownerId, List<InventoryItem> items) {
        // Enforce ownerId consistency and default validations
        List<InventoryItem> preparedItems = items.stream()
                .peek(item -> {
                    item.setOwnerId(ownerId);
                    if (item.getTier() == null) item.setTier(ItemTier.NORMAL);
                    
                    // Generate stats if missing
                    if (item.getMetadata() == null) {
                        item.setMetadata(generateStats(item.getItemId(), item.getTier()));
                    }
                })
                .collect(Collectors.toList());

        return inventoryItemRepository.saveAll(preparedItems);
    }

    // --- Retrieval ---

    public InventoryItem getItemById(UUID id) {
        return inventoryItemRepository.findById(id)
                .orElseThrow(() -> exceptionFactory.createNotFoundException("InventoryItem", id, InventoryError.ITEM_NOT_FOUND));
    }

    public List<InventoryItem> getItemsByOwner(UUID ownerId) {
        return inventoryItemRepository.findByOwnerId(ownerId);
    }

    public List<InventoryItem> getItemsByOwnerAndType(UUID ownerId, ItemType type) {
        return inventoryItemRepository.findByOwnerIdAndType(ownerId, type);
    }

    // --- Update ---

    @Transactional
    public InventoryItem updateItemMetadata(UUID ownerId, UUID itemId, Map<String, Object> newMetadata) {
        InventoryItem item = getItemById(itemId);
        validateOwnership(item, ownerId);

        item.setMetadata(newMetadata);
        return inventoryItemRepository.save(item);
    }

    // --- Deletion ---

    @Transactional
    public void deleteItem(UUID ownerId, UUID itemId) {
        InventoryItem item = getItemById(itemId);
        validateOwnership(item, ownerId);
        inventoryItemRepository.delete(item);
    }

    @Transactional
    public void deleteItems(UUID ownerId, List<UUID> itemIds) {
        // Bulk delete optimization could be done here, but we need ownership checks for safety.
        // For now, simple iteration is safer to reuse logic.
        itemIds.forEach(id -> deleteItem(ownerId, id));
    }

    // --- RNG Generation Logic ---

    private Map<String, Object> generateStats(String itemId, ItemTier tier) {
        Map<String, Object> stats = new HashMap<>();

        ItemDefinition def = itemRegistry.get(itemId);
        if (def == null) {
            log.warn("Stat Generation: Item Definition not found for ID: {}.", itemId);
            throw exceptionFactory.createValidationException("ItemDefinition", "item_id", itemId, InventoryError.ITEM_DEFINITION_NOT_FOUND);
        }

        // 1. GODSENT: Fixed Stats (Bypass RNG)
        if (tier == ItemTier.GODSENT) {
            //Load fixed stats from ItemDefinition
            if (def.getBaseStats() != null) {
                for (Map.Entry<String, StatRange> entry : def.getBaseStats().entrySet()) {
                    String statName = entry.getKey();
                    StatRange range = entry.getValue();

                    stats.put(statName, (int) range.getMax());
                }
            }

            return stats;
        }

        // 2. Lookup Definition


        // 3. Standard RNG Logic using Definition Ranges
        double multiplier = tier.getStatMultiplier();
        // Variance: +/- 10% (0.9 to 1.1)
        double variance = ThreadLocalRandom.current().nextDouble(0.9, 1.1);

        if (def.getBaseStats() != null) {
            for (Map.Entry<String, StatRange> entry : def.getBaseStats().entrySet()) {
                String statName = entry.getKey();
                StatRange range = entry.getValue();

                // Roll Base: Random between Min and Max
                double base = range.getMin() + (range.getMax() - range.getMin()) * ThreadLocalRandom.current().nextDouble();

                // Apply Tier Multiplier and Variance
                double finalVal = base * multiplier * variance;

                // Store as Integer for clean stats (e.g. 15 Armour, not 15.234)
                stats.put(statName, (int) Math.max(1, finalVal));
            }
        }

        return stats;
    }

    // --- Validation Helpers ---

    private void validateOwnership(InventoryItem item, UUID ownerId) {
        if (!item.getOwnerId().equals(ownerId)) {
            log.warn("Security Alert: Player {} tried to access Item {} belonging to {}", ownerId, item.getId(), item.getOwnerId());
            throw exceptionFactory.createCustomException(InventoryError.ITEM_NOT_OWNED);
        }
    }
}

