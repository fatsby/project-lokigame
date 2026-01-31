package com.theliems.lokigame.model.entity.hero;

import com.theliems.lokigame.model.enums.StatType;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(
        name = "hero_stats",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"hero_id", "stat_type"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeroStats {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hero_id", nullable = false)
    private Hero hero;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatType statType;

    @Column(nullable = false)
    private Double baseValue;

    @Column(nullable = false)
    @Builder.Default
    private Double finalValue = 0.0; // Calculated with equipment bonuses
}
