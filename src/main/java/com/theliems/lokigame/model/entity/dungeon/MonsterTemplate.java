package com.theliems.lokigame.model.entity.dungeon;

import com.theliems.lokigame.model.enums.StatType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Pre-defined monster blueprint with base stats and scaling coefficients.
 * Monsters are generated procedurally at runtime by scaling these templates.
 */
@Entity
@Table(name = "monster_templates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class MonsterTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    /**
     * Base stats at level 1.
     * Format: {HP: 100.0, ATK: 20.0, DEF: 10.0, SPEED: 50.0, CRIT_RATE: 0.05,
     * CRIT_DAMAGE: 1.5}
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "monster_template_base_stats", joinColumns = @JoinColumn(name = "template_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "stat_type")
    @Column(name = "stat_value")
    @Builder.Default
    private Map<StatType, Double> baseStats = new HashMap<>();

    /**
     * Additive stat growth per level.
     * Formula: finalStat = baseStat + (growthPerLevel * level)
     * This is applied BEFORE the compound scaling from DungeonConstants.
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "monster_template_stat_growth", joinColumns = @JoinColumn(name = "template_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "stat_type")
    @Column(name = "growth_value")
    @Builder.Default
    private Map<StatType, Double> statGrowthPerLevel = new HashMap<>();
}
