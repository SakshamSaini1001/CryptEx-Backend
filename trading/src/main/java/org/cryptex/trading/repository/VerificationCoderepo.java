package com.crypto.trading.repository;

import com.crypto.trading.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCoderepo extends JpaRepository<VerificationCode, Long> {

    public VerificationCode findByUserId(Long userId);
}
