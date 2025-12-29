package com.theliems.lokigame.model.entity.economy;

import com.theliems.lokigame.model.entity.player.Player;
import com.theliems.lokigame.model.enums.TransactionSource;
import com.theliems.lokigame.model.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "currency_transactions")
public class CurrencyTransaction {
    @Id
    @GeneratedValue
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    Player player;

    @Column(nullable = false)
    Long amount;

    @Column(nullable = false)
    Long balanceAfter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TransactionType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    TransactionSource source;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    LocalDateTime timestamp;
}
