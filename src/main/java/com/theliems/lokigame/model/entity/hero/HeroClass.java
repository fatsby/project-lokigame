package com.theliems.lokigame.model.entity.hero;

import com.theliems.lokigame.model.enums.StatType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "hero_classes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class HeroClass {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    /**
     * Base stat values for this class.
     * Format: {"HP": 100.0, "ATK": 50.0}
     */
    @ElementCollection
    @CollectionTable(name = "hero_class_base_stats", joinColumns = @JoinColumn(name = "hero_class_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "stat_type")
    @Column(name = "base_value")
    @Builder.Default
    private Map<StatType, Double> baseStats = new HashMap<>();

    /**
     * Stat modifiers as multipliers.
     * Format: {"ATK": 0.2, "CRIT_RATE": 0.15} means +20% ATK, +15% CRIT_RATE
     */
    @ElementCollection
    @CollectionTable(name = "hero_class_stat_modifiers", joinColumns = @JoinColumn(name = "hero_class_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "stat_type")
    @Column(name = "modifier_value")
    @Builder.Default
    private Map<StatType, Double> statModifiers = new HashMap<>();
}
