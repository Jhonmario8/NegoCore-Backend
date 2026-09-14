package com.negocore.domain.model;

import java.math.BigDecimal;

public record PayablePaymentRequest(
        BigDecimal amount,
        PayablePaymentMethod paymentMethod
) {
}
