package com.crypto.trading.controller;

import com.crypto.trading.domain.OrderType;
import com.crypto.trading.model.Coin;
import com.crypto.trading.model.Order;
import com.crypto.trading.model.User;
import com.crypto.trading.model.WalletTransaction;
import com.crypto.trading.request.CreateOrderRequest;
import com.crypto.trading.service.CoinService;
import com.crypto.trading.service.OrderService;
import com.crypto.trading.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CoinService coinService;

//    @Autowired
//    private WalletTransactionService walletTransactionService;

    @PostMapping("/pay")
    public ResponseEntity<Order> payOrderPayment(@RequestHeader("Authorization") String jwt,
                                                 @RequestBody CreateOrderRequest req) throws Exception {
        User user = userService.findUserbyJwt(jwt);
        Coin coin = coinService.findById(req.getCoinId());

        Order order = orderService.processOrder(coin, req.getQuantity(),req.getOrderType(),user);

        return ResponseEntity.ok(order);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@RequestHeader("Authorization") String jwtToken,
                                              @PathVariable Long orderId) throws Exception {
        User user = userService.findUserbyJwt(jwtToken);
        Order order = orderService.getorderbyid(orderId);

        if (order.getUser().getId().equals(user.getId())){
            return ResponseEntity.ok(order);
        }
        else{
            throw new Exception("no access");
        }
    }

    @GetMapping()
    public ResponseEntity<List<Order>> getAllOrdersForUser(@RequestHeader("Authorization") String jwt,
                                                           @RequestParam(required = false) OrderType orderType,
                                                           @RequestHeader(required = false) String asset_symbol) throws Exception {
        Long userId = userService.findUserbyJwt(jwt).getId();
        List<Order> userOrders = orderService.getAllOrdersOfUser(userId,orderType,asset_symbol);
        return ResponseEntity.ok(userOrders);
    }
}
