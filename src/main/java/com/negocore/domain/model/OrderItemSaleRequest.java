package com.negocore.domain.model;

import java.math.BigDecimal;

public record OrderItemSaleRequest(
        PaymentMethod paymentMethod,
        BigDecimal paidAmount
) {
}
