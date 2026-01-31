package com.theliems.lokigame.model.entity.inventory;

import com.theliems.lokigame.model.entity.equipment.EquipmentStat;
import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.enums.EquipmentType;
import com.theliems.lokigame.model.enums.Rarity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Concrete InventoryItem subclass for equipment.
 * Stored in separate 'equipment_items' table linked via foreign key to
 * 'inventory_items'.
 * Absorbs all fields from the old Equipment entity.
 */
@Entity
@Table(name = "equipment_items")
@PrimaryKeyJoinColumn(name = "id")
@Getter
@Setter
@NoArgsConstructor
public class EquipmentItem extends InventoryItem {

    @Enumerated(EnumType.STRING)
    @Column(name = "equipment_type", nullable = false)
    private EquipmentType equipmentType;

    @Column(nullable = false)
    private Integer level = 1;

    @OneToMany(mappedBy = "equipmentItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<EquipmentStat> baseStats = new ArrayList<>();

    @OneToMany(mappedBy = "equipmentItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<EquipmentStat> randomStats = new ArrayList<>();

    /**
     * Full constructor for programmatic creation.
     */
    public EquipmentItem(Player owner, EquipmentType equipmentType, Rarity rarity, Integer level) {
        this.setOwner(owner);
        this.setRarity(rarity);
        this.equipmentType = equipmentType;
        this.level = level;
    }

    @Override
    public String getDisplayName() {
        return getRarity().name() + " " + equipmentType.name();
    }

    /**
     * Helper to set bidirectional relationship for base stats.
     */
    public void addBaseStat(EquipmentStat stat) {
        stat.setEquipmentItem(this);
        stat.setIsBaseStat(true);
        this.baseStats.add(stat);
    }

    /**
     * Helper to set bidirectional relationship for random stats.
     */
    public void addRandomStat(EquipmentStat stat) {
        stat.setEquipmentItem(this);
        stat.setIsBaseStat(false);
        this.randomStats.add(stat);
    }
}
