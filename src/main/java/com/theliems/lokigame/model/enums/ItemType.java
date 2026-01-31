package com.theliems.lokigame.model.enums;

public enum ItemType {
    VISUAL,
    WEAPON,
    ARMOR,
    ACCESSORY,
    EQUIPMENT; // Added for generic equipment items

    public boolean isEquipment() {
        return this == WEAPON || this == ARMOR || this == ACCESSORY || this == EQUIPMENT;
    }
}
