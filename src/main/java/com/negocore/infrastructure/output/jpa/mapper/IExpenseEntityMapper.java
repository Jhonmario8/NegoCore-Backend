package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.Expense;
import com.negocore.infrastructure.output.jpa.entity.ExpenseEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IExpenseEntityMapper {

    Expense toDomain(ExpenseEntity expenseEntity);


    ExpenseEntity toEntity(Expense expense);

}
