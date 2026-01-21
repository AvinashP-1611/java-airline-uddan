package com.airline.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "wallet_transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wallet_id", nullable = false)
    private Wallet wallet;

    @Column(nullable = false)
    private Double amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type; // CREDIT, DEBIT

    @Column(nullable = false)
    private String description;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    @PrePersist
    protected void onCreate() {
        this.transactionTime = LocalDateTime.now();
    }

    public enum TransactionType {
        CREDIT, DEBIT
    }
}
