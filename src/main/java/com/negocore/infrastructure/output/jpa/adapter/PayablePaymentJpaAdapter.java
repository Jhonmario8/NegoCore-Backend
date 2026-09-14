package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.PayablePayment;
import com.negocore.domain.spi.IPayablePaymentPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.IPayablePaymentEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IPayablePaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PayablePaymentJpaAdapter implements IPayablePaymentPersistencePort {

    private final IPayablePaymentRepository repository;
    private final IPayablePaymentEntityMapper mapper;

    @Override
    public PayablePayment save(PayablePayment payablePayment) {
        return mapper.toDomain(repository.save(mapper.toEntity(payablePayment)));
    }
}
