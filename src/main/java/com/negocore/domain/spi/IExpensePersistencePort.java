package com.negocore.domain.spi;

import com.negocore.domain.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public interface IExpensePersistencePort {

    Expense save(Expense expense);

    BigDecimal sumAmountByBusinessIdAndCreatedAtBetween(
            Long businessId,
            LocalDateTime from,
            LocalDateTime to
    );

}