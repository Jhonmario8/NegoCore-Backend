package com.negocore.application.handler;

import com.negocore.application.dto.request.ExpenseRequestDTO;
import com.negocore.application.dto.response.ExpenseResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface IExpenseHandler {

    ExpenseResponseDTO registerExpense(Long businessId, ExpenseRequestDTO expenseRequestDTO);
    List<ExpenseResponseDTO> findExpenses(
            Long businessId,
            LocalDate from,
            LocalDate to
    );
}
