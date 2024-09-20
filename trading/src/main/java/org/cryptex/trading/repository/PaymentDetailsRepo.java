package com.crypto.trading.repository;

import com.crypto.trading.model.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentDetailsRepo extends JpaRepository<PaymentDetails, Long> {
    PaymentDetails findByUserId(Long userId);
}
