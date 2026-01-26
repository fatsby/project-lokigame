package com.theliems.lokigame.model.entity.hero;

import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.entity.system.AuditMetaData;
import com.theliems.lokigame.model.enums.EquipmentSlot;
import com.theliems.lokigame.model.enums.HeroGender;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "heroes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Hero {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID heroId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerId")
    private Player owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "heroClassId")
    private HeroClass heroClass;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private HeroGender gender;

    @Column(name = "first_name", nullable = false, length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 50)
    private String lastName;

    @Column(nullable = false)
    private int rarity; // 1-7 Stars

//    @Column(name = "origin_world_id", nullable = false)
//    private String originWorldId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "worldId")
    private World originWorld;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "origin_id")
    private Origin origin;

    @Column(nullable = false)
    @Builder.Default
    private Integer level = 1;

    @Column(nullable = false)
    @Builder.Default
    private Integer star = 1; // 1-7 stars

    @Column(nullable = false)
    @Builder.Default
    private Long experience = 0L;

    /**
     * Multiplier for idle XP gain.
     * Generated randomly, each Hero will have unique willpower amount.
     */
    @Column(nullable = false)
    @Builder.Default
    private Double willPower = 1.0;

    /**
     * Exp per second for calculating XP gain with idling
     * Base value, each Hero share the same expPerSecond
     */
    @Column(nullable = false)
    @Builder.Default
    private Double expPerSecond = 0.001;

    /**
     * Stats rolled based on Class + Rarity + World.
     * Stored as JSONB in Postgres for flexibility.
     * Expected keys: health, armour, abilityPower
     */
//    @Type(JsonBinaryType.class)
//    @Column(columnDefinition = "jsonb")
//    private Map<String, Double> stats;

    /**
     * Visual identifiers for body features (cosmetic only).
     * Stored as JSONB.
     * Expected keys: hair_id, face_id
     */
//    @Type(JsonBinaryType.class)
//    @Column(columnDefinition = "jsonb")
//    private Map<String, String> visuals;

    /**
     * Active Equipment Loadout.
     * Maps Slot -> Equipment UUID (Instance ID).
     * Allows fetching the specific equipment instance (with its specific stats) for
     * stat calculation.
     */
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    @Builder.Default
    private Map<EquipmentSlot, UUID> equipment = new java.util.HashMap<>();

    /**
     * Hero stats - one per StatType
     */
    @OneToMany(mappedBy = "hero", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private java.util.List<HeroStats> stats = new java.util.ArrayList<>();

    /**
     * Random seed for uniqueness - ensures each hero is procedurally unique
     */
    @Column(nullable = false)
    private Long randomSeed;

    @Embedded
    @Builder.Default
    AuditMetaData auditMetaData = new AuditMetaData();
}