package com.negocore.domain.spi;

import com.negocore.domain.model.CashRegister;
import com.negocore.domain.model.CashRegisterStatus;

import java.util.Optional;

public interface ICashRegisterPersistencePort {

    CashRegister save(CashRegister cashRegister);
    Boolean existsOpenCashRegisterByBusinessIdAndStatus(Long businessId, CashRegisterStatus status);
    Optional<CashRegister> findOpenCashRegisterByBusinessIdAndStatus(Long businessId, CashRegisterStatus status);
}
