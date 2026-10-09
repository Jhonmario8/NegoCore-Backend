package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.PayablePayment;
import com.negocore.domain.spi.IPayablePaymentPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.IPayablePaymentEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IPayablePaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PayablePaymentJpaAdapter implements IPayablePaymentPersistencePort {

    private final IPayablePaymentRepository repository;
    private final IPayablePaymentEntityMapper mapper;

    @Override
    public PayablePayment save(PayablePayment payablePayment) {
        return mapper.toDomain(repository.save(mapper.toEntity(payablePayment)));
    }

    @Override
    public List<PayablePayment> findAllByPayableId(Long payableId) {
        return repository.findAllByPayableId(payableId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<PayablePayment> findById(Long id) {
        return repository.findById(id).map(mapper::toDomain);
    }
}
