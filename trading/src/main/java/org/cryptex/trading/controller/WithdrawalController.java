package com.crypto.trading.controller;

import com.crypto.trading.domain.WalletTransactionType;
import com.crypto.trading.model.User;
import com.crypto.trading.model.Wallet;
import com.crypto.trading.model.WalletTransaction;
import com.crypto.trading.model.Withdrawal;
import com.crypto.trading.service.UserService;
import com.crypto.trading.service.WalletService;
import com.crypto.trading.service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class WithdrawalController {
    @Autowired
    private WithdrawalService withdrawalService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @PostMapping("/api/withdrawal/{amount}")
    public ResponseEntity<?> withdrawalRequest(
            @PathVariable Long amount,
            @RequestHeader("Authorization") String jwt) {
        User user =userService.findUserbyJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);

        Withdrawal withdrawal=withdrawalService.requestWithdrawal(amount,user);
        walletService.addBalance(wallet,-withdrawal.getAmount());

//        WalletTransaction walletTransaction = walletTransactionService.createTransaction(wallet, WalletTransactionType.WITHDRAWAL,null,"Bank Account Withdrawal",withdrawal.getAmount());

        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    @PatchMapping("api/admin/withdrawal/{id}/proceed/{accept}")
    public ResponseEntity<?> proceedWithdrawal(
            @PathVariable Long id,
            @PathVariable boolean accept,
            @RequestHeader("Authorization") String jwt
    ) throws Exception {
        User user =userService.findUserbyJwt(jwt);
        Withdrawal withdrawal = withdrawalService.processWithdrawal(id,accept);
        Wallet userWallet = walletService.getUserWallet(user);
        if (!accept){
            walletService.addBalance(userWallet,withdrawal.getAmount());
        }

        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }

    @GetMapping("api/withdrawal")
    public ResponseEntity<List<Withdrawal>> getWithdrawalHistory(@RequestHeader("Authorization") String jwt){
        User user =userService.findUserbyJwt(jwt);
        List<Withdrawal> withdrawals = withdrawalService.getuserWithdrawalHistory(user);
        return new ResponseEntity<>(withdrawals, HttpStatus.OK);
    }

    @GetMapping("/api/admin/withdrawal")
    public ResponseEntity<List<Withdrawal>> getAdminWithdrawalHistory(@RequestHeader("Authorization") String jwt){
        User user =userService.findUserbyJwt(jwt);
        List<Withdrawal> withdrawal = withdrawalService.getAllWithdrawalRequest();

        return new ResponseEntity<>(withdrawal, HttpStatus.OK);
    }
}
