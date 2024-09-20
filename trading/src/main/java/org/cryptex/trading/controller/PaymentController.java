package com.crypto.trading.controller;

import com.crypto.trading.domain.PaymentMethod;
import com.crypto.trading.model.PaymentOrder;
import com.crypto.trading.model.User;
import com.crypto.trading.response.PaymentResponse;
import com.crypto.trading.service.PaymentService;
import com.crypto.trading.service.UserService;
import com.razorpay.RazorpayException;
import jdk.jshell.spi.ExecutionControl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PaymentController {
    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/api/payment/amount/{amount}")
    public ResponseEntity<PaymentResponse> paymentHandler
            (
             @PathVariable Long amount,
             @RequestHeader("Authorization")String jwt) throws RazorpayException {
        User user = userService.findUserbyJwt(jwt);
        PaymentResponse paymentResponse;
        PaymentOrder order=paymentService.createOrder(user,amount);

        paymentResponse=paymentService.createRazorPayPayment(user,amount, order.getId());

        return new ResponseEntity<>(paymentResponse, HttpStatus.CREATED);
    }
}
