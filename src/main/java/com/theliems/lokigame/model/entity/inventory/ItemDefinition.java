package com.theliems.lokigame.model.entity.inventory;

import com.theliems.lokigame.model.entity.hero.StatRange;
import com.theliems.lokigame.model.enums.EquipmentSlot;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ItemDefinition {
    private String id;
    private String name;
    private ItemType type;
    private EquipmentSlot slot;
    private List<String> classRestriction;
    private boolean isDefault;
    private Map<String, StatRange> baseStats;
}
