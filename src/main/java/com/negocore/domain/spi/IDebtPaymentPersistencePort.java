package com.negocore.domain.spi;


import com.negocore.domain.model.DebtPayment;

public interface IDebtPaymentPersistencePort {

    Boolean existsByDebtId(Long debtId);
    DebtPayment save(DebtPayment debtPayment);

}
