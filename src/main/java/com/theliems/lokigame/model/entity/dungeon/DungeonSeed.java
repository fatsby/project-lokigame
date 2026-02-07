package com.theliems.lokigame.model.entity.dungeon;

import com.theliems.lokigame.model.entity.system.AuditMetaData;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

/**
 * Persisted entity storing dungeon seeds per player per level.
 * Enables deterministic dungeon regeneration and replay of cleared dungeons.
 * 
 * Level Gating Rules:
 * 1. Player can only attempt level <= highestClearedLevel + 1
 * 2. Frontier level (highestClearedLevel + 1) cannot be re-seeded
 * 3. Cleared levels can be replayed using stored seeds
 */
@Entity
@Table(name = "dungeon_seeds", uniqueConstraints = {
        @UniqueConstraint(name = "uk_dungeon_seed_player_world_level", columnNames = { "player_id", "world_id",
                "dungeon_level" })
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class DungeonSeed {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "player_id", nullable = false)
    private UUID playerId;

    @Column(name = "world_id", nullable = false)
    private UUID worldId;

    @Column(name = "dungeon_level", nullable = false)
    private Integer dungeonLevel;

    /**
     * Random seed for deterministic dungeon generation.
     * Using the same seed will always produce the same monster composition.
     */
    @Column(nullable = false)
    private Long seed;

    /**
     * Whether this dungeon has been cleared (beaten).
     * Once cleared, the seed is preserved for replay purposes.
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean cleared = false;

    @Embedded
    @Builder.Default
    private AuditMetaData auditMetaData = new AuditMetaData();
}
