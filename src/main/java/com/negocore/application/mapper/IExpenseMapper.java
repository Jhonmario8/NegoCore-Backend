package com.negocore.application.mapper;

import com.negocore.application.dto.request.ExpenseRequestDTO;
import com.negocore.application.dto.response.ExpenseResponseDTO;
import com.negocore.domain.model.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IExpenseMapper {

    Expense toDomain(ExpenseRequestDTO expenseRequestDTO);

    ExpenseResponseDTO toResponseDTO(Expense expense);


}
