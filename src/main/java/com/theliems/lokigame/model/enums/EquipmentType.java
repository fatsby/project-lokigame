package com.theliems.lokigame.model.enums;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public enum EquipmentType {
    WEAPON,
    HELMET,
    ARMOR,
    BOOTS,
    RING,
    NECKLACE;

    public ItemType toItemType() {
        if (this == WEAPON) {
            return ItemType.WEAPON;
        } else if (this == HELMET || this == ARMOR || this == BOOTS) {
            return ItemType.ARMOR;
        } else if (this == RING || this == NECKLACE) {
            return ItemType.ACCESSORY;
        }
        // Fallback or throw an exception if an unmapped EquipmentType is encountered
        throw new IllegalArgumentException("EquipmentType " + this + " cannot be mapped to an ItemType.");
    }
}
