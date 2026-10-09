package com.negocore.domain.spi;


import com.negocore.domain.model.DebtPayment;

import java.util.List;
import java.util.Optional;

public interface IDebtPaymentPersistencePort {

    Boolean existsByDebtId(Long debtId);
    DebtPayment save(DebtPayment debtPayment);
    List<DebtPayment> findAllByDebtId(Long debtId);
    Optional<DebtPayment> findById(Long id);

}
