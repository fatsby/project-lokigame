package com.theliems.lokigame.model.entity.inventory;

import com.theliems.lokigame.model.entity.system.AuditMetaData;
import com.theliems.lokigame.model.enums.ItemTier;
import com.theliems.lokigame.model.enums.ItemType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "inventory_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    /**
     * The Reference ID to the static ItemDefinition (in JSON/ItemRegistry).
     * This acts as a "Logical Foreign Key" to the in-memory game data.
     */
    @Column(name = "item_id", nullable = false)
    private String itemId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemTier tier;

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;

    @Embedded
    @Builder.Default
    private AuditMetaData auditMetaData = new AuditMetaData();
}
