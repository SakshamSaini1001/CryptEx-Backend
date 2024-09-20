package com.crypto.trading.service;

import com.crypto.trading.domain.OrderStatus;
import com.crypto.trading.domain.OrderType;
import com.crypto.trading.model.*;
import com.crypto.trading.repository.OrderItemRepo;
import com.crypto.trading.repository.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {
    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private WalletService walletService;

    @Autowired
    private OrderItemRepo orderItemRepo;

    @Autowired
    private AssetService assetService;

    public Order createorder(User user, OrderItem orderItem, OrderType orderType) {
        double price = orderItem.getCoin().getCurrentPrice() * orderItem.getQuantity();

        Order order = new Order();
        order.setUser(user);
        order.setOrderType(orderType);
        order.setOrderItem(orderItem);
        order.setPrice(BigDecimal.valueOf(price));
        order.setTimestamp(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        return orderRepo.save(order);
    }

    public Order getorderbyid(Long orderId) throws Exception {
        return orderRepo.findById(orderId).orElseThrow(() -> new Exception("Order not found"));
    }

    public List<Order> getAllOrdersOfUser(Long userId, OrderType orderType, String assetSymbol) {
        return orderRepo.findByUserId(userId);
    }

    private OrderItem createOrderItem(Coin coin, double quantity, double buyprice, double sellprice) {
        OrderItem orderItem = new OrderItem();
        orderItem.setCoin(coin);
        orderItem.setQuantity(quantity);
        orderItem.setBuyprice(buyprice);
        orderItem.setSellprice(sellprice);
        return orderItemRepo.save(orderItem);
    }

    public Order buyAsset(Coin coin, double quantity, User user) throws Exception {
        if (quantity <= 0)
            throw new Exception("quantity should be greater than 0");
        double buyprice = coin.getCurrentPrice();

        OrderItem orderItem = createOrderItem(coin, quantity, buyprice, 0);

        Order order = createorder(user, orderItem, OrderType.BUY);
        orderItem.setOrder(order);

        walletService.payOrderPayment(order, user);
        order.setStatus(OrderStatus.SUCCESS);
        order.setOrderType(OrderType.BUY);
        Order savedOrder = orderRepo.save(order);

//      create asset
        Asset oldAsset = assetService.findAssetByUserIdAndCoinId(order.getUser().getId(), order.getOrderItem().getCoin().getId());
        if (oldAsset == null)
            assetService.createAsset(user, orderItem.getCoin(), orderItem.getQuantity());
        else
            assetService.updateAsset(oldAsset.getId(), quantity);

        return savedOrder;
    }

    public Order sellAsset(Coin coin, double quantity, User user) throws Exception {
        if (quantity <= 0)
            throw new Exception("quantity should be greater than 0");
        double sellprice = coin.getCurrentPrice();

        Asset assettosell = assetService.findAssetByUserIdAndCoinId(user.getId(), coin.getId());
        double buyprice = assettosell.getBuyPrice();

        if (assettosell != null){
        OrderItem orderItem = createOrderItem(coin, quantity, buyprice, sellprice);

        Order order = createorder(user, orderItem, OrderType.SELL);
        orderItem.setOrder(order);

        if (assettosell.getQuantity() >= quantity) {
            order.setStatus(OrderStatus.SUCCESS);
            order.setOrderType(OrderType.SELL);
            Order savedOrder = orderRepo.save(order);
            walletService.payOrderPayment(order, user);

            Asset updatedasset = assetService.updateAsset(assettosell.getId(), -quantity);

            if (updatedasset.getQuantity() * coin.getCurrentPrice() <= 1) {
                assetService.deleteAsset(updatedasset.getId());
            }
            return savedOrder;
        }
        throw new Exception("Insufficient quantity to sell");
    }
    throw new

    Exception("Asset Not Found");
}
    @Transactional
    public Order processOrder(Coin coin,double quantity,OrderType orderType,User user) throws Exception {
        if (orderType.equals(OrderType.BUY)) {
            return buyAsset(coin,quantity,user);
        } else if (orderType.equals(OrderType.SELL)) {
            return sellAsset(coin,quantity,user);
        }
        else{
            throw  new Exception("Invalid Order Type");
        }
    }
}
