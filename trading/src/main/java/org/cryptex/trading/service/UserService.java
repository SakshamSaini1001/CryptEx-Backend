package com.crypto.trading.service;

import com.crypto.trading.domain.VerificationType;
import com.crypto.trading.model.User;

public interface UserService {

    public User findUserbyJwt(String jwt);
    public User findUserbyEmail(String email);
    public User findUserbyId(Long userid);

    public User enableTwoFactorAuthentication(VerificationType verificationType,String sendTo, User user);

    User updatePassword(User user,String newPassword);

}
