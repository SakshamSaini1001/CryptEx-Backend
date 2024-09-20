package com.crypto.trading.service;

import com.crypto.trading.model.TwoFactorOTP;
import com.crypto.trading.model.User;
import com.crypto.trading.repository.TwoFactorOtpRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service

public class TwoFactorServiceImpl implements TwoFactorOtpService{

    @Autowired
    private TwoFactorOtpRepo twoFactorOtpRepo;
    @Override
    public TwoFactorOTP createTwoFactorOtp(User user, String otp, String jwt) {
        UUID uuid = UUID.randomUUID();

        String id = uuid.toString();

        TwoFactorOTP twoFactorOTP=new TwoFactorOTP();
        twoFactorOTP.setOtp(otp);
        twoFactorOTP.setJwt(jwt);
        twoFactorOTP.setId(id);
        twoFactorOTP.setUser(user);
        return twoFactorOtpRepo.save(twoFactorOTP);
    }

    @Override
    public TwoFactorOTP findbyuser(Long userId) {

        return twoFactorOtpRepo.findByUserId(userId);
    }

    @Override
    public TwoFactorOTP findbyid(String id) {
        Optional<TwoFactorOTP> opt = twoFactorOtpRepo.findById(id);
        return opt.orElse(null);
    }

    @Override
    public boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOTP, String otp) {
        return twoFactorOTP.getOtp().equals(otp);
    }

    @Override
    public void deleteTwoFactorOtp(TwoFactorOTP twoFactorOtp) {
        twoFactorOtpRepo.delete(twoFactorOtp);
    }
}
