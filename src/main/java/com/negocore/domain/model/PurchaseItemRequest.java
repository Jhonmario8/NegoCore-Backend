package com.negocore.domain.model;

import java.math.BigDecimal;

public record PurchaseItemRequest(
        Long productId,
        Integer quantity,
        BigDecimal unitCost
) {
}
