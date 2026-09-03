package com.negocore.domain.api;

import com.negocore.domain.model.Expense;

public interface IExpenseServicePort {

    Expense registerExpense(Long businessId, Expense expense);

}
