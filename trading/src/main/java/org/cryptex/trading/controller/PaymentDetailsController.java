package com.crypto.trading.controller;

import com.crypto.trading.model.PaymentDetails;
import com.crypto.trading.model.User;
import com.crypto.trading.service.PaymentDetailsService;
import com.crypto.trading.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentDetailsController {

    @Autowired
    private UserService userService;
    @Autowired
    private PaymentDetailsService paymentDetailsService;

    @PostMapping("/payment-details")
    public ResponseEntity<PaymentDetails> addPaymentDetails(@RequestBody PaymentDetails paymentDetails, @RequestHeader("Authorization")String jwt) throws Exception {
        User user = userService.findUserbyJwt(jwt);

        PaymentDetails paymentDetails1 = paymentDetailsService.addPaymentDetails(
                paymentDetails.getAccountNumber(), paymentDetails.getAccountHolderName(), paymentDetails.getIfsc(), paymentDetails.getBankName(), user);
        return new ResponseEntity<>(paymentDetails1, HttpStatus.CREATED);
    }

    @GetMapping("/payment-details")
    public ResponseEntity<PaymentDetails> getPaymentDetails(@RequestHeader("Authorization")String jwt) throws Exception {
        User user = userService.findUserbyJwt(jwt);
        PaymentDetails paymentDetails=paymentDetailsService.getUserPaymentDetails(user);
        return new ResponseEntity<>(paymentDetails, HttpStatus.OK);
    }
}
