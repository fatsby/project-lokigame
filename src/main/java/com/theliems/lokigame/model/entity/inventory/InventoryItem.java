package com.theliems.lokigame.model.entity.inventory;

import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.entity.system.AuditMetaData;
import com.theliems.lokigame.model.enums.Rarity;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Map;
import java.util.UUID;

/**
 * Abstract base class for all inventory items using JOINED inheritance.
 * Each subclass (EquipmentItem, ConsumableItem, etc.) gets its own table.
 */
@Entity
@Table(name = "inventory_items")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Player owner;

    /**
     * Rarity applies to all inventory items (equipment, consumables, etc.)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rarity rarity;

    /**
     * Optional metadata for future extensibility.
     */
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb", nullable = true)
    private Map<String, Object> metadata;

    @Embedded
    private AuditMetaData auditMetaData = new AuditMetaData();

    /**
     * Returns a display-friendly name for the item.
     * Each subclass implements based on its specific type.
     */
    public abstract String getDisplayName();
}
