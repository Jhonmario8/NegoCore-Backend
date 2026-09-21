package com.negocore.domain.model;

import java.math.BigDecimal;
import java.util.List;

public record OrderConversionRequest(
        Long providerId,
        List<OrderConversionItemRequest> unitCosts,
        PaymentMethod paymentMethod,
        BigDecimal paidAmount,
        BigDecimal shippingCost
) {
}
