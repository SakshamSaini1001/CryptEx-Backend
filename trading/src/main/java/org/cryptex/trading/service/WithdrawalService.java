package com.crypto.trading.service;

import com.crypto.trading.domain.WithdrawalStatus;
import com.crypto.trading.model.User;
import com.crypto.trading.model.Wallet;
import com.crypto.trading.model.Withdrawal;
import com.crypto.trading.repository.WithdrawalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WithdrawalService {

    @Autowired
    private WithdrawalRepo withdrawalRepo;

    public Withdrawal requestWithdrawal(Long amount, User user){
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setAmount(amount);
        withdrawal.setUser(user);
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        return withdrawalRepo.save(withdrawal);
    }
    public Withdrawal processWithdrawal(Long withdrawalId, boolean accept) throws Exception {
        Optional<Withdrawal> withdrawal = withdrawalRepo.findById(withdrawalId);
        if(withdrawal.isEmpty()){
            throw new Exception("withdrawal not found");
        }
        Withdrawal withdrawal1 = withdrawal.get();
        withdrawal1.setDate(LocalDateTime.now());

        if (accept){
            withdrawal1.setStatus(WithdrawalStatus.SUCCESS);
        }
        else {
            withdrawal1.setStatus(WithdrawalStatus.PENDING);
        }
        return withdrawalRepo.save(withdrawal1);
    }
    public List<Withdrawal> getuserWithdrawalHistory(User user){
        return withdrawalRepo.findByUserId(user.getId());
    }
    public List<Withdrawal> getAllWithdrawalRequest(){
        return withdrawalRepo.findAll();
    }
}
