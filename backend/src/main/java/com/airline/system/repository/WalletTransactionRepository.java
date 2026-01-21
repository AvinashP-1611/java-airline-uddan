package com.airline.system.repository;

import com.airline.system.entity.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {
    List<WalletTransaction> findByWalletIdOrderByTransactionTimeDesc(Long walletId);

    Page<WalletTransaction> findByWalletId(Long walletId, Pageable pageable);
}
