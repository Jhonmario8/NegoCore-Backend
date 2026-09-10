package com.negocore.application.handler;

import com.negocore.application.dto.request.ExpenseRequestDTO;
import com.negocore.application.dto.response.ExpenseResponseDTO;
import com.negocore.application.mapper.IExpenseMapper;
import com.negocore.domain.api.IExpenseServicePort;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseHandler implements IExpenseHandler{

    private final IExpenseServicePort expenseServicePort;
    private final IExpenseMapper mapper;

    @Override
    public ExpenseResponseDTO registerExpense(Long businessId, ExpenseRequestDTO expenseRequestDTO) {
        return mapper.toResponseDTO(expenseServicePort.registerExpense(businessId, mapper.toDomain(expenseRequestDTO)));
    }

    @Override
    public List<ExpenseResponseDTO> findExpenses(
            Long businessId,
            LocalDate from,
            LocalDate to
    ) {

        LocalDateTime fromDateTime =
                from != null
                        ? from.atStartOfDay()
                        : null;

        LocalDateTime toDateTime =
                to != null
                        ? to.plusDays(1).atStartOfDay()
                        : null;

        return expenseServicePort.findExpenses(
                        businessId,
                        fromDateTime,
                        toDateTime
                )
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

}
