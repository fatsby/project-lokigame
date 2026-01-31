package com.theliems.lokigame.model.entity.equipment;

import com.theliems.lokigame.model.enums.StatType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

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
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatType statType;

    @Column(nullable = false)
    private Double value;

    @Column(nullable = false)
    @Builder.Default
    private Boolean isBaseStat = true; // true for baseStats, false for randomStats
}
