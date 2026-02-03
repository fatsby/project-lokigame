package com.theliems.lokigame.model.entity.equipment;

import com.theliems.lokigame.model.entity.inventory.EquipmentItem;
import com.theliems.lokigame.model.enums.StatType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Represents a single stat on an equipment item.
 * Can be either a base stat (always present) or a random stat (varies by
 * rarity).
 */
@Entity
@Table(name = "equipment_stats")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentStat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_item_id", nullable = false)
    private EquipmentItem equipmentItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatType statType;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isBaseStat = true;
}
