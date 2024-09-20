package com.crypto.trading.service;

import com.crypto.trading.domain.VerificationType;
import com.crypto.trading.model.User;
import com.crypto.trading.model.VerificationCode;
import com.crypto.trading.repository.VerificationCoderepo;
import com.crypto.trading.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VerificationCodeService {

    @Autowired
    private VerificationCoderepo verificationCoderepo;

    public VerificationCode sendVerificationCode(User user, VerificationType verificationType) {
        VerificationCode verificationCode1= new VerificationCode();
        verificationCode1.setOtp(OtpUtils.generateOtp());
        verificationCode1.setVerificationType(verificationType);
        verificationCode1.setUser(user);

        return verificationCoderepo.save(verificationCode1);
    }

    public VerificationCode getVerificationCodeById(Long id) {
        Optional<VerificationCode> verificationCode = verificationCoderepo.findById(id);
        if(verificationCode.isPresent()) {
            return verificationCode.get();
        }
        throw new RuntimeException("Verification code not found");
    }

    public VerificationCode getVerificationCodeByUser(Long userId) {
        return verificationCoderepo.findByUserId(userId);
    }

    public void deleteVerificationCodeById(VerificationCode verificationCode) {
        verificationCoderepo.delete(verificationCode);
    }
}
