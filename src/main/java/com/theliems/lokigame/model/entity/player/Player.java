package com.theliems.lokigame.model.entity.player;

import com.theliems.lokigame.model.entity.system.AuditMetaData;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Builder
@Table(name = "players")
public class Player {
    @Id
    @GeneratedValue
    UUID id;

    @Column(nullable = false, unique = true)
    String email;

    @Column(nullable = false, unique = true)
    String username;

    @Column(nullable = false)
    String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    Role role;

    @Column(nullable = false)
    @Builder.Default
    Long currency = 0L;

    @Embedded
    @Builder.Default
    AuditMetaData auditMetaData = new AuditMetaData();
}
