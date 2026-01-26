package com.theliems.lokigame.model.entity.dungeon;

import com.theliems.lokigame.model.enums.StatType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "monsters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Monster {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dungeon_id", nullable = false)
    private Dungeon dungeon;

    @Column(nullable = false)
    @Builder.Default
    private Integer level = 1;

    /**
     * Monster stats stored as JSONB for flexibility.
     * Format: {"HP": 1000.0, "ATK": 100.0, "DEF": 50.0, "SPEED": 80.0, "CRIT_RATE": 0.1, "CRIT_DAMAGE": 1.5}
     */
    @ElementCollection
    @CollectionTable(name = "monster_stats", joinColumns = @JoinColumn(name = "monster_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "stat_type")
    @Column(name = "stat_value")
    @Builder.Default
    private Map<StatType, Double> stats = new HashMap<>();
}
