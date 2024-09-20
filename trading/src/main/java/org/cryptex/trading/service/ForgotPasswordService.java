package com.crypto.trading.service;

import com.crypto.trading.domain.VerificationType;
import com.crypto.trading.model.ForgotPasswordToken;
import com.crypto.trading.model.User;
import com.crypto.trading.repository.ForgotPasswordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ForgotPasswordService {

    @Autowired
    private ForgotPasswordRepo forgotPasswordRepo;

    public ForgotPasswordToken createToken(User user, String id, String otp, VerificationType verificationType, String sendTo){
        ForgotPasswordToken token = new ForgotPasswordToken();
        token.setUser(user);
        token.setSendTo(sendTo);
        token.setVerificationType(verificationType);
        token.setOtp(otp);
        token.setId(id);
        return forgotPasswordRepo.save(token);
    }

    public ForgotPasswordToken findById( String id){
        Optional<ForgotPasswordToken> token=forgotPasswordRepo.findById(id);
        return token.orElse(null);
    }

    public ForgotPasswordToken findByUser(Long userid){
        return forgotPasswordRepo.findByUserId(userid);
    }

    public void deleteToken(ForgotPasswordToken token){
        forgotPasswordRepo.delete(token);
    }
}
