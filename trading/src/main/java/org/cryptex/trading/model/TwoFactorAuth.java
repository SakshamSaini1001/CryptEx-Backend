package com.crypto.trading.model;

import com.crypto.trading.domain.VerificationType;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Data
public class TwoFactorAuth {
    private boolean enabled = false;
    private VerificationType sendto;
}
