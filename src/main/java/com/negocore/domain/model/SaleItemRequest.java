package com.negocore.domain.model;

public record SaleItemRequest(
        Long productId,
        Integer quantity
) {
}
