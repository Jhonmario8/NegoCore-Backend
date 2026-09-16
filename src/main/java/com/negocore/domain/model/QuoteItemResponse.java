package com.negocore.domain.model;

import java.math.BigDecimal;

public record QuoteItemResponse(
        Long productId,
        String name,
        String sku,
        String imageUrl,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
