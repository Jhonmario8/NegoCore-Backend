package com.negocore.application.handler;

import com.negocore.application.dto.request.ExpenseRequestDTO;
import com.negocore.application.dto.response.ExpenseResponseDTO;

public interface IExpenseHandler {

    ExpenseResponseDTO registerExpense(Long businessId, ExpenseRequestDTO expenseRequestDTO);

}
