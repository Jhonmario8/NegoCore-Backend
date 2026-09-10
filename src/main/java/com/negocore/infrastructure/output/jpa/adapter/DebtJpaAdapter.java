package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.Debt;
import com.negocore.domain.model.DebtStatus;
import com.negocore.domain.spi.IDebtPersistencePort;
import com.negocore.infrastructure.output.jpa.entity.DebtEntity;
import com.negocore.infrastructure.output.jpa.mapper.IDebtEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IDebtRepository;
import com.negocore.infrastructure.output.jpa.specification.DebtSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DebtJpaAdapter implements IDebtPersistencePort {

    private final IDebtRepository repository;
    private final IDebtEntityMapper mapper;

    @Override
    public Debt save(Debt debt) {
        return mapper.toDomain(repository.save(mapper.toEntity(debt)));
    }

    @Override
    public Optional<Debt> findBySaleId(Long saleId) {
        return repository.findBySaleId(saleId).map(mapper::toDomain);
    }

    @Override
    public Optional<Debt> findById(Long debtId) {
        return repository.findById(debtId).map(mapper::toDomain);
    }

    @Override
    public List<Debt> findAllByFilters(
            Long businessId,
            DebtStatus status,
            Long clientId
    ) {

        Specification<DebtEntity> specification =
                DebtSpecification.withFilters(
                        businessId,
                        status,
                        clientId
                );

        return repository.findAll(specification)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }


}
