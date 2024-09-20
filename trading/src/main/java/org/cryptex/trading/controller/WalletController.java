package com.crypto.trading.controller;

import com.crypto.trading.model.*;
import com.crypto.trading.response.PaymentResponse;
import com.crypto.trading.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PaymentService paymentService;

    @GetMapping("/api/wallet")
    public ResponseEntity<Wallet> getUserWallet(@RequestHeader("Authorization") String jwt){
        User user = userService.findUserbyJwt(jwt);
        Wallet wallet = walletService.getUserWallet(user);

        return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
    }

    @PutMapping("/api/wallet/{walletId}/transfer")
    public ResponseEntity<Wallet> wallettowallettransfer(@RequestHeader("Authorization") String jwt, @PathVariable Long walletId, @RequestBody WalletTransaction request) throws Exception {
        User sender = userService.findUserbyJwt(jwt);
        Wallet receiver = walletService.findById(walletId);
        Wallet wallet=walletService.wallettowallettransfer(sender,receiver, request.getAmount());
        return new ResponseEntity<>(wallet,HttpStatus.ACCEPTED);

    }

    @PutMapping("/api/wallet/order/{orderId}/pay")
    public ResponseEntity<Wallet> payOrderPayment(@RequestHeader("Authorization") String jwt, @PathVariable Long orderId) throws Exception {

        User user = userService.findUserbyJwt(jwt);
        Order order = orderService.getorderbyid(orderId);

        Wallet wallet = walletService.payOrderPayment(order,user);
        return new ResponseEntity<>(wallet,HttpStatus.ACCEPTED);

    }

    @PutMapping("/api/wallet/deposit")
    public ResponseEntity<Wallet> addMoneyToWallet(@RequestHeader("Authorization") String jwt,@RequestParam(name="order_id")Long orderId,
                                                   @RequestParam(name = "payment_id") String payment_Id) throws Exception {

        User user = userService.findUserbyJwt(jwt);
        Wallet wallet=walletService.getUserWallet(user);
        PaymentOrder order=paymentService.getPaymentOrderById(orderId);
        Boolean status=paymentService.proceedPayment(order,payment_Id);
        if (wallet.getBalance()==null){
            wallet.setBalance(BigDecimal.valueOf(0));
        }
        if (status){
            wallet=walletService.addBalance(wallet,order.getAmount());
        }
        return new ResponseEntity<>(wallet,HttpStatus.ACCEPTED);

    }
}
