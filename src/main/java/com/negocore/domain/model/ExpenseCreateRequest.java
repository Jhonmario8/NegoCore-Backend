package com.negocore.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExpenseCreateRequest(
        String description,
        String category,
        BigDecimal amount,
        Boolean paid,
        PayeeType payeeType,
        Long providerId,
        String payeeName,
        LocalDate dueDate
) {
}
