package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.Expense;
import com.negocore.domain.spi.IExpensePersistencePort;
import com.negocore.infrastructure.output.jpa.entity.ExpenseEntity;
import com.negocore.infrastructure.output.jpa.mapper.IExpenseEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IExpenseRepository;
import com.negocore.infrastructure.output.jpa.specification.ExpenseSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ExpenseJpaAdapter implements IExpensePersistencePort {

    private final IExpenseRepository expenseRepository;
    private final IExpenseEntityMapper mapper;

    @Override
    public Expense save(Expense expense) {
        return mapper.toDomain(expenseRepository.save(mapper.toEntity(expense)));
    }

    @Override
    public List<Expense> findAllByFilters(
            Long businessId,
            LocalDateTime from,
            LocalDateTime to
    ) {

        Specification<ExpenseEntity> specification =
                ExpenseSpecification.withFilters(
                        businessId,
                        from,
                        to
                );

        return expenseRepository.findAll(specification)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }


    @Override
    public BigDecimal sumAmountByBusinessIdAndCreatedAtBetween(Long businessId, LocalDateTime from, LocalDateTime to) {
        return expenseRepository.sumAmountByBusinessIdAndCreatedAtRange(businessId, from, to);
    }
}
