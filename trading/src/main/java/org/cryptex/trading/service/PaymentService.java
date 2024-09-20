package com.crypto.trading.service;

import com.crypto.trading.domain.PaymentMethod;
import com.crypto.trading.domain.PaymentOrderStatus;
import com.crypto.trading.model.PaymentOrder;
import com.crypto.trading.model.User;
import com.crypto.trading.repository.PaymentOrderRepo;
import com.crypto.trading.response.PaymentResponse;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
    @Autowired
    private PaymentOrderRepo paymentOrderRepo;
    @Value(("${razorpay.api.key}"))
    private String apiKey;
    @Value("${razorpay.api.secret}")
    private String apiSecretKey;

    public PaymentOrder createOrder(User user,Long amount) {
        PaymentOrder paymentOrder=new PaymentOrder();
        paymentOrder.setUser(user);
        paymentOrder.setAmount(amount);
        paymentOrder.setStatus(PaymentOrderStatus.PENDING);

        return paymentOrderRepo.save(paymentOrder);
    }

    public PaymentOrder getPaymentOrderById(Long id) throws Exception {
        return paymentOrderRepo.findById(id).orElseThrow(()->new Exception("PaymentOrder not found"));
    }

    public Boolean proceedPayment(PaymentOrder paymentOrder,String paymentId) throws RazorpayException {
        if (paymentOrder.getStatus()==null){
            paymentOrder.setStatus(PaymentOrderStatus.PENDING);
        }
        if(paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)){
            RazorpayClient razorpay=new RazorpayClient(apiKey,apiSecretKey);
            Payment payment=razorpay.payments.fetch(paymentId);

            Integer amount=payment.get("amount");
            String status = payment.get("status");

            if (status.equals("captured")){
                paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                return true;
            }
            paymentOrder.setStatus(PaymentOrderStatus.FAILED);
            paymentOrderRepo.save(paymentOrder);
            return false;
        }
//        paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
//        paymentOrderRepo.save(paymentOrder);
//        return true;
        return false;
    }

    public PaymentResponse createRazorPayPayment(User user,Long amount,Long order_id) throws RazorpayException {

       Long Amount=amount*100;
       try {
           RazorpayClient razorpay=new RazorpayClient(apiKey,apiSecretKey);
           JSONObject paymentLinkRequest=new JSONObject();
           paymentLinkRequest.put("amount",amount);
           paymentLinkRequest.put("currency","INR");

           JSONObject customer=new JSONObject();
           customer.put("name",user.getFullName());

           customer.put("email",user.getEmail());
           paymentLinkRequest.put("customer",customer);

           JSONObject notify=new JSONObject();
           notify.put("email",true);
           paymentLinkRequest.put("notify",notify);

           paymentLinkRequest.put("reminder_enable",true);

           paymentLinkRequest.put("callback_url","http://localhost:5173/wallet?order_id=" + order_id);
           paymentLinkRequest.put("callback_method","get");

           PaymentLink payment=razorpay.paymentLink.create(paymentLinkRequest);

           String paymentLinkId = payment.get("id");
           String paymentLinkUrl = payment.get("short_url");

           PaymentResponse response = new PaymentResponse();
           response.setPaymenturl(paymentLinkUrl);

           return response;
       }
       catch (RazorpayException e){
           System.out.println("Error creating payment link :" + e.getMessage());
           throw new RazorpayException(e.getMessage());
       }
    }
}
