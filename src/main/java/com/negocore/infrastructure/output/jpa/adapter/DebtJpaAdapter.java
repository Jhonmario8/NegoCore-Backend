package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.Debt;
import com.negocore.domain.spi.IDebtPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.IDebtEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IDebtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebtJpaAdapter implements IDebtPersistencePort {

    private final IDebtRepository repository;
    private final IDebtEntityMapper mapper;

    @Override
    public Debt save(Debt debt) {
        return mapper.toDomain(repository.save(mapper.toEntity(debt)));
    }
}
