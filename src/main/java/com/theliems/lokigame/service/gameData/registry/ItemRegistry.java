package com.theliems.lokigame.service.gameData.registry;

import com.theliems.lokigame.model.entity.inventory.ItemDefinition;
import com.theliems.lokigame.model.enums.EquipmentSlot;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ItemRegistry implements DataRegistry {
    
    private final Map<String, ItemDefinition> itemMap = new ConcurrentHashMap<>();

    public void add(ItemDefinition item) {
        itemMap.put(item.getId(), item);
    }

    public ItemDefinition get(String id) {
        return itemMap.get(id);
    }
    
    public ItemDefinition getBaseItem(EquipmentSlot slot, String classId) {
        return itemMap.values().stream()
                .filter(i -> i.isDefault() && i.getSlot() == slot)
                .filter(i -> i.getClassRestriction() == null || i.getClassRestriction().isEmpty() || i.getClassRestriction().contains(classId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void clear() {
        itemMap.clear();
    }
}
