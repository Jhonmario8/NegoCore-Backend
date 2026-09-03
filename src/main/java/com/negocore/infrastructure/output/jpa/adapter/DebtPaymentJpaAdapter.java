package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.spi.IDebtPaymentPersistencePort;
import com.negocore.infrastructure.output.jpa.repository.IDebtPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class DebtPaymentJpaAdapter implements IDebtPaymentPersistencePort {

    private final IDebtPaymentRepository debtPaymentRepository;


    @Override
    public Boolean existsByDebtId(Long debtId) {
        return debtPaymentRepository.existsByDebtId(debtId);
    }
}
