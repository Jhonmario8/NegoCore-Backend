package com.negocore.application.mapper;

import com.negocore.application.dto.request.ExpenseRequestDTO;
import com.negocore.application.dto.response.ExpenseResponseDTO;
import com.negocore.domain.model.Expense;
import com.negocore.domain.model.ExpenseCreateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IExpenseMapper {

    ExpenseCreateRequest toDomain(ExpenseRequestDTO expenseRequestDTO);

    ExpenseResponseDTO toResponseDTO(Expense expense);


}
