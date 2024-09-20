package com.crypto.trading.controller;

import com.crypto.trading.request.ForgotPasswordTokenRequest;
import com.crypto.trading.domain.VerificationType;
import com.crypto.trading.model.ForgotPasswordToken;
import com.crypto.trading.model.User;
import com.crypto.trading.model.VerificationCode;
import com.crypto.trading.request.ResetPasswordRequest;
import com.crypto.trading.response.ApiResponse;
import com.crypto.trading.response.AuthResponse;
import com.crypto.trading.service.EmailService;
import com.crypto.trading.service.ForgotPasswordService;
import com.crypto.trading.service.UserService;
import com.crypto.trading.service.VerificationCodeService;
import com.crypto.trading.utils.OtpUtils;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwt) {
        User user = userService.findUserbyJwt(jwt);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @PostMapping("/api/users/verification/{verificationtype}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(
            @RequestHeader("Authorization") String jwt,
            @PathVariable VerificationType verificationtype) throws MessagingException {

        User user = userService.findUserbyJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

        if (verificationCode == null) {
            verificationCode = verificationCodeService.sendVerificationCode(user, verificationtype);
        }
        if(verificationtype.equals(VerificationType.Email)){
            emailService.sendverificationotpemail(user.getEmail(),verificationCode.getOtp());
        }

        return new ResponseEntity<>("Verification OTP Sent Successfully", HttpStatus.OK);
    }

    @PatchMapping("/api/users/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuthenticetion(@PathVariable String otp,@RequestHeader("Authorization") String jwt) throws Exception {
        User user = userService.findUserbyJwt(jwt);

        VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());
        String sendTo = verificationCode.getVerificationType().equals(VerificationType.Email) ?
                verificationCode.getEmail() : verificationCode.getMobile();

        boolean isverified = verificationCode.getOtp().equals(otp);

        if (isverified) {
            User updateduser = userService.enableTwoFactorAuthentication(verificationCode.getVerificationType(), sendTo, user);
        verificationCodeService.deleteVerificationCodeById(verificationCode);
        return new ResponseEntity<>(updateduser, HttpStatus.OK);
    }
        throw new Exception("Wrong OTP");
    }

    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp(
            @RequestBody ForgotPasswordTokenRequest request) throws MessagingException {

        User user = userService.findUserbyEmail(request.getSendTo());
        String otp = OtpUtils.generateOtp();
        UUID uuid = UUID.randomUUID();
        String id = uuid.toString();

        ForgotPasswordToken token = forgotPasswordService.findByUser(user.getId());
        if (token == null) {
            token=forgotPasswordService.createToken(user,id,otp,request.getVerificationType(),request.getSendTo());
        }

        if(request.getVerificationType().equals(VerificationType.Email)){
            emailService.sendverificationotpemail(user.getEmail(),token.getOtp());
        }

        AuthResponse response = new AuthResponse();
        response.setSession(token.getId());
        response.setMessage("Password Reset OTP sent Successfully");
        return new ResponseEntity<>(response , HttpStatus.OK);
    }
    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetpassword(@RequestParam String id, @RequestBody ResetPasswordRequest request, @RequestHeader("Authorization") String jwt) throws Exception {
        ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findById(id);

        boolean isverified = forgotPasswordToken.getOtp().equals(request.getOtp());

        if (isverified){
            userService.updatePassword(forgotPasswordToken.getUser(),request.getPassword());
            ApiResponse response = new ApiResponse();
            response.setMessage("Password Updated Successfully");
            return new ResponseEntity<>(response,HttpStatus.ACCEPTED);
        }
        throw new Exception("Wrong Otp");
    }
}
