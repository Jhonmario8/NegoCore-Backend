package com.negocore.domain.spi;




public interface IDebtPaymentPersistencePort {

    Boolean existsByDebtId(Long debtId);


}
