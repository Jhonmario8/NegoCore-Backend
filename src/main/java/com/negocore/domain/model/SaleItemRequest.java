package com.negocore.domain.model;

import java.math.BigDecimal;

public record SaleItemRequest(
        Long productId,
        Integer quantity,
        BigDecimal unitPrice
) {
}
