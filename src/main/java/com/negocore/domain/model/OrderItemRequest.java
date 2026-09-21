package com.negocore.domain.model;

import java.math.BigDecimal;

public record OrderItemRequest(
        Long productId,
        Integer quantity,
        Long clientId,
        String requesterName,
        BigDecimal unitCost,
        BigDecimal salePrice
) {
}
