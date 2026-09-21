package com.negocore.domain.model;

import java.math.BigDecimal;

public record OrderConversionItemRequest(
        Long productId,
        BigDecimal unitCost
) {
}
