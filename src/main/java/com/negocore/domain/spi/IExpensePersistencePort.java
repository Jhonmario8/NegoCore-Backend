package com.negocore.domain.spi;

import com.negocore.domain.model.Expense;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;



public interface IExpensePersistencePort {

    Expense save(Expense expense);

    List<Expense> findAllByFilters(
            Long businessId,
            LocalDateTime from,
            LocalDateTime to
    );

    BigDecimal sumAmountByBusinessIdAndCreatedAtBetween(
            Long businessId,
            LocalDateTime from,
            LocalDateTime to
    );

}