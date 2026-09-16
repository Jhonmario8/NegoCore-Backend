package com.negocore.domain.model;

import java.math.BigDecimal;

public record QuoteItemRequest(
        Long productId,
        Integer quantity,
        BigDecimal unitPrice
) {
}
