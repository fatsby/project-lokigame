package com.theliems.lokigame.model.entity.dungeon;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Table(name = "drop_tables")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class DropTable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dungeon_id", nullable = false, unique = true)
    private Dungeon dungeon;

    /**
     * Base gold reward
     */
    @Column(nullable = false)
    @Builder.Default
    private Long baseGold = 100L;

    /**
     * Gold multiplier based on dungeon level
     */
    @Column(nullable = false)
    @Builder.Default
    private Double goldMultiplier = 1.0;

    /**
     * Equipment drop chance (0.0 to 1.0)
     */
    @Column(nullable = false)
    @Builder.Default
    private Double equipmentDropChance = 0.3;

    /**
     * Material drop chance (0.0 to 1.0)
     */
    @Column(nullable = false)
    @Builder.Default
    private Double materialDropChance = 0.5;

    /**
     * Base XP reward for completing this dungeon
     */
    @Column(nullable = false)
    @Builder.Default
    private Long baseXp = 50L;

    /**
     * XP multiplier based on dungeon difficulty
     */
    @Column(nullable = false)
    @Builder.Default
    private Double xpMultiplier = 1.0;
}
