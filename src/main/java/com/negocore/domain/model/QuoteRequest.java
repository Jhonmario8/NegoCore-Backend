package com.negocore.domain.model;

import java.util.List;

public record QuoteRequest(
        String clientName,
        Integer validityDays,
        List<QuoteItemRequest> items
) {
}
