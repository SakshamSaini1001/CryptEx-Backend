package com.crypto.trading.service;

import com.crypto.trading.domain.OrderType;
import com.crypto.trading.model.Order;
import com.crypto.trading.model.User;
import com.crypto.trading.model.Wallet;
import com.crypto.trading.model.WalletTransaction;
import com.crypto.trading.repository.WalletRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private WalletRepo walletRepo;

    public Wallet getUserWallet(User user) {
        Wallet wallet=walletRepo.findByUserId(user.getId());
        if (wallet==null) {
            wallet = new Wallet();
            wallet.setUser(user);
            walletRepo.save(wallet);
        }
        return  wallet;
    }

    public Wallet addBalance(Wallet wallet,Long amount) {
        BigDecimal balance=wallet.getBalance();
        BigDecimal newbalance=balance.add(BigDecimal.valueOf(amount));
        wallet.setBalance(newbalance);
        return walletRepo.save(wallet);
    }

    public Wallet findById(Long id) throws Exception {
        Optional<Wallet> wallet=walletRepo.findById(id);
        if (wallet.isPresent()){
            return wallet.get();
        }
        throw new Exception("Wallet Not Found");
    }

    public Wallet wallettowallettransfer(User sender,Wallet receiverWallet,Long amount) throws Exception {
        Wallet senderWallet= getUserWallet(sender);

        if (senderWallet.getBalance().compareTo(BigDecimal.valueOf(amount))<0) {
            throw new Exception("Insufficient Balance...");
        }
        BigDecimal senderBalance=senderWallet.getBalance().subtract(BigDecimal.valueOf(amount));
        senderWallet.setBalance(senderBalance);
        walletRepo.save(senderWallet);

        BigDecimal receiverBalance = receiverWallet.getBalance().add(BigDecimal.valueOf(amount));
        receiverWallet.setBalance(receiverBalance);
        walletRepo.save(receiverWallet);
        return senderWallet;
    }

    public Wallet payOrderPayment(Order order, User user) throws Exception {
        Wallet wallet=getUserWallet(user);

        if (order.getOrderType().equals(OrderType.BUY)) {
            BigDecimal newBalance = wallet.getBalance().subtract(order.getPrice());
            if (newBalance.compareTo(order.getPrice()) < 0) {
                throw new Exception("Insufficient Funds");
            }
            wallet.setBalance(newBalance);
        }
            else{
                BigDecimal newbalance=wallet.getBalance().add(order.getPrice());
                wallet.setBalance(newbalance);
            }
            walletRepo.save(wallet);
        return wallet;
    }
}
