package com.airline.system.controller;

import com.airline.system.entity.User;
import com.airline.system.entity.Wallet;
import com.airline.system.repository.UserRepository;
import com.airline.system.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
@RequestMapping("/api/user/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/balance")
    public ResponseEntity<?> getBalance() {
        try {
            User user = getCurrentUser();
            Wallet wallet = walletService.getOrCreateWallet(user);
            return ResponseEntity.ok(Map.of("balance", wallet.getBalance()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/transactions")
    public ResponseEntity<?> getTransactions() {
        try {
            User user = getCurrentUser();
            return ResponseEntity.ok(walletService.getTransactionHistory(user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
}
