package com.theliems.lokigame.model.entity.inventory;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.theliems.lokigame.model.entity.hero.StatRange;
import com.theliems.lokigame.model.enums.EquipmentSlot;
import com.theliems.lokigame.model.enums.ItemType;
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

    @JsonProperty("isDefault")
    private boolean defaultItem;

    private Map<String, StatRange> baseStats;
}
