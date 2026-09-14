package com.negocore.domain.api;

import com.negocore.domain.model.Expense;
import com.negocore.domain.model.ExpenseCreateRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface IExpenseServicePort {

    Expense registerExpense(Long businessId, ExpenseCreateRequest expenseCreateRequest);
    List<Expense> findExpenses(
            Long businessId,
            LocalDateTime from,
            LocalDateTime to
    );
}
