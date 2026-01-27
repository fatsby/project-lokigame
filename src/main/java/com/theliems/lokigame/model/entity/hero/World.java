package com.theliems.lokigame.model.entity.hero;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Table(name = "worlds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class World {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID worldId;
    @Column(length = 50)
    private String name;
    private String description;

    // For Hero Generation RNG
    private double rarityWeight; // Higher = More Common (e.g., 100 vs 5)

    // The "Bonus" logic
    @Column(name = "stat_multiplier")
    private double statMultiplier;

    // For Future Phase: Dungeon Scaling
        private double dungeonDifficultyMod;
}
