package com.theliems.lokigame.service.gameData.registry;

import com.theliems.lokigame.model.entity.inventory.ItemDefinition;
import com.theliems.lokigame.model.enums.EquipmentSlot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ItemRegistry implements DataRegistry {

    private final Map<String, ItemDefinition> itemMap = new ConcurrentHashMap<>();

    /**
     * Pre-computed index for O(1) default item lookup.
     * Key: "SLOT:classId" (e.g., "BODY:mage")
     * Also stores "SLOT:*" for items with no class restriction.
     */
    private final Map<String, ItemDefinition> defaultItemIndex = new ConcurrentHashMap<>();

    public void add(ItemDefinition item) {
        itemMap.put(item.getId(), item);
        log.debug("ItemRegistry: Added item {} (slot={}, isDefault={})",
                item.getId(), item.getSlot(), item.isDefaultItem());

        // Build default item index for O(1) lookup
        if (item.isDefaultItem() && item.getSlot() != null) {
            if (item.getClassRestriction() == null || item.getClassRestriction().isEmpty()) {
                // No restriction - available to all classes
                String key = buildIndexKey(item.getSlot(), "*");
                defaultItemIndex.put(key, item);
                log.info("ItemRegistry: Indexed default item {} -> {}", key, item.getId());
            } else {
                // Index for each allowed class
                for (String classId : item.getClassRestriction()) {
                    String key = buildIndexKey(item.getSlot(), classId);
                    defaultItemIndex.put(key, item);
                    log.info("ItemRegistry: Indexed default item {} -> {}", key, item.getId());
                }
            }
        }
    }

    public ItemDefinition get(String id) {
        return itemMap.get(id);
    }

    /**
     * O(1) lookup for default items by slot and class.
     * First checks for class-specific default, then falls back to universal
     * default.
     */
    public ItemDefinition getBaseItem(EquipmentSlot slot, String classId) {
        // Try class-specific first
        ItemDefinition item = defaultItemIndex.get(buildIndexKey(slot, classId));
        if (item != null) {
            return item;
        }
        // Fall back to universal (no class restriction)
        return defaultItemIndex.get(buildIndexKey(slot, "*"));
    }

    private String buildIndexKey(EquipmentSlot slot, String classId) {
        return slot.name() + ":" + classId;
    }

    @Override
    public void clear() {
        itemMap.clear();
        defaultItemIndex.clear();
    }
}
