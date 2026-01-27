package com.theliems.lokigame.model.entity.hero;

import com.theliems.lokigame.model.enums.StatType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "origins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Origin {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    /**
     * Passive stat modifiers as JSONB.
     * Format: {"HP": 0.1, "ATK": 0.05} means +10% HP, +5% ATK
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "origin_stat_modifiers", joinColumns = @JoinColumn(name = "origin_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "stat_type")
    @Column(name = "modifier_value")
    @Builder.Default
    private Map<StatType, Double> statModifiers = new HashMap<>();
}
