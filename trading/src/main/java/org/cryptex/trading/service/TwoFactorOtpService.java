package com.crypto.trading.service;

import com.crypto.trading.model.TwoFactorOTP;
import com.crypto.trading.model.User;
import org.springframework.stereotype.Service;

public interface TwoFactorOtpService {

    TwoFactorOTP createTwoFactorOtp(User user,String otp,String jwt);

    TwoFactorOTP findbyuser(Long userId);

    TwoFactorOTP findbyid(String id);

    boolean verifyTwoFactorOtp(TwoFactorOTP twoFactorOTP , String otp);

    void deleteTwoFactorOtp(TwoFactorOTP twoFactorOTP);
}
