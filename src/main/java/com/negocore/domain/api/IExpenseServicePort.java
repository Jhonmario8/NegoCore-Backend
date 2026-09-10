package com.negocore.domain.api;

import com.negocore.domain.model.Expense;

import java.time.LocalDateTime;
import java.util.List;

public interface IExpenseServicePort {

    Expense registerExpense(Long businessId, Expense expense);
    List<Expense> findExpenses(
            Long businessId,
            LocalDateTime from,
            LocalDateTime to
    );
}
