package com.theliems.lokigame.model.entity.hero;

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
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "hero_class", nullable = false)
    private String heroClass; // Matches "id" in classes.json

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private HeroGender gender;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(nullable = false)
    private int rarity; // 1-7 Stars

    @Column(name = "origin_world_id", nullable = false)
    private String originWorldId;

    @Column(name = "alive", nullable = false)
    @Builder.Default
    private Boolean alive = true;

    @Column(nullable = false)
    @Builder.Default
    private Integer level = 1;

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
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Double> stats;

    /**
     * Visual identifiers for body features (cosmetic only).
     * Stored as JSONB.
     * Expected keys: hair_id, face_id
     */
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> visuals;

    /**
     * Active Equipment Loadout.
     * Maps Slot -> InventoryItem UUID (Instance ID).
     * Allows fetching the specific item instance (with its specific stats/tier) for
     * stat calculation.
     */
    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private Map<EquipmentSlot, UUID> equipment;

    @Embedded
    @Builder.Default
    AuditMetaData auditMetaData = new AuditMetaData();
}