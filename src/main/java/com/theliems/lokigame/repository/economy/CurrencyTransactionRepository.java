package com.theliems.lokigame.repository.economy;

import com.theliems.lokigame.model.entity.economy.CurrencyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CurrencyTransactionRepository extends JpaRepository<CurrencyTransaction, UUID> {
}
