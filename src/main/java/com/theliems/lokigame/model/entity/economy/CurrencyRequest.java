package com.theliems.lokigame.model.entity.economy;

import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.entity.system.AuditMetaData;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@Table(name = "currency_requests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class CurrencyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Column(nullable = false)
    private Long amount;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private RequestStatus status = RequestStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by")
    private Player reviewedBy;

    @Column(length = 500)
    private String adminNotes;

    @Embedded
    @Builder.Default
    private AuditMetaData auditMetaData = new AuditMetaData();

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}
