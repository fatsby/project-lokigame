package com.theliems.lokigame.model.entity.dungeon;

import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Transient procedurally generated dungeon (not persisted).
 * Generated on-demand using a DungeonSeed for deterministic recreation.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dungeon {

    /**
     * Unique identifier for this dungeon instance.
     */
    private UUID id;

    /**
     * Display name of the dungeon.
     */
    private String name;

    /**
     * Optional description/flavor text.
     */
    private String description;

    /**
     * Dungeon difficulty level.
     */
    private Integer level;

    /**
     * The World this dungeon belongs to.
     */
    private UUID worldId;

    /**
     * The seed used to generate this dungeon.
     */
    private Long seed;

    /**
     * List of scaled monster instances for this dungeon.
     */
    @Builder.Default
    private List<Monster> monsters = new ArrayList<>();

    /**
     * Calculated drop table for rewards.
     */
    private DropTable dropTable;
}
