package com.negocore.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record QuoteResponse(
        String businessName,
        String address,
        String phone,
        String email,
        String sellerName,
        String clientName,
        LocalDate quoteDate,
        LocalDate expirationDate,
        List<QuoteItemResponse> items,
        BigDecimal total
) {
}
