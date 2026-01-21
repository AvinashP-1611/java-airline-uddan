package com.airline.system.service;

import com.airline.system.entity.User;
import com.airline.system.entity.Wallet;
import com.airline.system.entity.WalletTransaction;
import com.airline.system.repository.WalletRepository;
import com.airline.system.repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WalletService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletTransactionRepository transactionRepository;

    public Wallet getOrCreateWallet(User user) {
        return walletRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Wallet newWallet = new Wallet();
                    newWallet.setUser(user);
                    newWallet.setBalance(0.0);
                    return walletRepository.save(newWallet);
                });
    }

    @Transactional
    public void addFunds(User user, Double amount, String description) {
        Wallet wallet = getOrCreateWallet(user);
        wallet.setBalance(wallet.getBalance() + amount);
        walletRepository.save(wallet);

        createTransaction(wallet, amount, WalletTransaction.TransactionType.CREDIT, description);
    }

    @Transactional
    public void deductFunds(User user, Double amount, String description) {
        Wallet wallet = getOrCreateWallet(user);
        if (wallet.getBalance() < amount) {
            throw new RuntimeException("Insufficient wallet balance");
        }
        wallet.setBalance(wallet.getBalance() - amount);
        walletRepository.save(wallet);

        createTransaction(wallet, amount, WalletTransaction.TransactionType.DEBIT, description);
    }

    private void createTransaction(Wallet wallet, Double amount, WalletTransaction.TransactionType type,
            String description) {
        WalletTransaction txn = new WalletTransaction();
        txn.setWallet(wallet);
        txn.setAmount(amount);
        txn.setType(type);
        txn.setDescription(description);
        transactionRepository.save(txn);
    }

    public List<WalletTransaction> getTransactionHistory(User user) {
        Wallet wallet = getOrCreateWallet(user);
        return transactionRepository.findByWalletIdOrderByTransactionTimeDesc(wallet.getId());
    }
}
