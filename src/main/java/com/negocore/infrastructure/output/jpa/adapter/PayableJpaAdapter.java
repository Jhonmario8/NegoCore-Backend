package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.Payable;
import com.negocore.domain.model.PayableStatus;
import com.negocore.domain.model.PayeeType;
import com.negocore.domain.spi.IPayablePersistencePort;
import com.negocore.infrastructure.output.jpa.entity.PayableEntity;
import com.negocore.infrastructure.output.jpa.mapper.IPayableEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IPayableRepository;
import com.negocore.infrastructure.output.jpa.specification.PayableSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PayableJpaAdapter implements IPayablePersistencePort {

    private final IPayableRepository repository;
    private final IPayableEntityMapper mapper;

    @Override
    public Payable save(Payable payable) {
        return mapper.toDomain(repository.save(mapper.toEntity(payable)));
    }

    @Override
    public Optional<Payable> findById(Long payableId) {
        return repository.findById(payableId).map(mapper::toDomain);
    }

    @Override
    public List<Payable> findAllByFilters(
            Long businessId,
            PayableStatus status,
            PayeeType payeeType,
            Long providerId
    ) {
        Specification<PayableEntity> specification =
                PayableSpecification.withFilters(
                        businessId,
                        status,
                        payeeType,
                        providerId
                );

        return repository.findAll(specification)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }
}
