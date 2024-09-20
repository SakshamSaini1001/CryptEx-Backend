package com.crypto.trading.controller;

import com.crypto.trading.config.JwtProvider;
import com.crypto.trading.model.TwoFactorOTP;
import com.crypto.trading.model.User;
import com.crypto.trading.repository.UserRepository;
import com.crypto.trading.response.AuthResponse;
import com.crypto.trading.service.CustomUserDetailService;
import com.crypto.trading.service.EmailService;
import com.crypto.trading.service.TwoFactorOtpService;
import com.crypto.trading.service.WatchListService;
import com.crypto.trading.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.http.HttpResponse;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomUserDetailService customUserDetailService;
    @Autowired
    private TwoFactorOtpService twoFactorOtpService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private WatchListService watchListService;

    @PostMapping("signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {

        User isemailexist = userRepository.findByemail(user.getEmail());

        if(isemailexist != null) {
            throw new Exception("Email is already used with another account");
        }

        User newUser = new User();
        newUser.setFullName(user.getFullName());
        newUser.setPassword(user.getPassword());
        newUser.setEmail(user.getEmail());

        User savedUser = userRepository.save(newUser);

        watchListService.createWatchList(savedUser);

        Authentication auth = new UsernamePasswordAuthenticationToken(user.getEmail(),
                user.getPassword()
        );

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);
        AuthResponse response = new AuthResponse();
        response.setJwt(jwt);
        response.setStatus(true);
        response.setMessage("Successfully login");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("signin")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception {

        String username = user.getEmail();
        String password = user.getPassword();

        Authentication auth = authenticate(username,password);

        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        User authuser = userRepository.findByemail(username);

        if(user.getTwoFactorAuth().isEnabled()){
            AuthResponse response = new AuthResponse();
            response.setMessage("Two Factor Auth Enabled");
            response.setTwoFactorAuthEnabled(true);
            String otp = OtpUtils.generateOtp();
            TwoFactorOTP oldTwoFactorOTP = twoFactorOtpService.findbyuser(authuser.getId());
            if(oldTwoFactorOTP != null){
                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOTP);
            }

            TwoFactorOTP newTwoFactorOTP = twoFactorOtpService.createTwoFactorOtp(authuser,otp,jwt);

            emailService.sendverificationotpemail(username,otp);

            response.setSession(newTwoFactorOTP.getId());
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        }

        AuthResponse response = new AuthResponse();
        response.setJwt(jwt);
        response.setStatus(true);
        response.setMessage("Successfully registered");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    private Authentication authenticate(String username, String password) {
        UserDetails userDetails = customUserDetailService.loadUserByUsername(username);

        if(userDetails==null){
            throw new BadCredentialsException("invalid username");
        }
        if(!password.equals(userDetails.getPassword())){
            throw new BadCredentialsException("invalid password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails,password,userDetails.getAuthorities());
    }

    @PostMapping("/two-factor/otp/{otp}")
    public ResponseEntity<AuthResponse> verifysigninotp(@PathVariable String otp,@RequestParam String id) throws Exception {

        TwoFactorOTP twoFactorOTP=twoFactorOtpService.findbyid(id);

        if(twoFactorOtpService.verifyTwoFactorOtp(twoFactorOTP,otp)){
            AuthResponse response = new AuthResponse();
            response.setMessage("Two Factor Authentication Verified");
            response.setTwoFactorAuthEnabled(true);
            response.setJwt(twoFactorOTP.getJwt());
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        throw new Exception("Invalid OTP");
    }
}
