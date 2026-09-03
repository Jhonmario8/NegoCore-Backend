package com.negocore.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SaleRequest(
        List<SaleItemRequest> saleItems,
        PaymentMethod paymentMethod,
        BigDecimal paidAmount,
        Long clientId
) {
}
