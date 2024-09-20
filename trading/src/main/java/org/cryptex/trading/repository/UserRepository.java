package com.crypto.trading.repository;

import com.crypto.trading.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByemail(String email);
}
