package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.DebtPayment;
import com.negocore.domain.spi.IDebtPaymentPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.IDebtPaymentEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IDebtPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DebtPaymentJpaAdapter implements IDebtPaymentPersistencePort {

    private final IDebtPaymentRepository debtPaymentRepository;
    private final IDebtPaymentEntityMapper mapper;

    @Override
    public Boolean existsByDebtId(Long debtId) {
        return debtPaymentRepository.existsByDebtId(debtId);
    }

    @Override
    public DebtPayment save(DebtPayment debtPayment) {
        return mapper.toDomain(debtPaymentRepository.save(mapper.toEntity(debtPayment)));
    }
}
