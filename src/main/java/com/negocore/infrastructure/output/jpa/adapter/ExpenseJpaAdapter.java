package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.Expense;
import com.negocore.domain.spi.IExpensePersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.IExpenseEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExpenseJpaAdapter implements IExpensePersistencePort {

    private final IExpenseRepository expenseRepository;
    private final IExpenseEntityMapper mapper;

    @Override
    public Expense save(Expense expense) {
        return mapper.toDomain(expenseRepository.save(mapper.toEntity(expense)));
    }
}
