package com.negocore.domain.model;

import java.math.BigDecimal;

public record DebtCreateRequest(
        BigDecimal amount,
        DebtPaymentMethod paymentMethod
) {
}
