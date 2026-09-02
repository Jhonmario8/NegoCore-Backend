package com.negocore.domain.spi;

import com.negocore.domain.model.CashRegister;
import com.negocore.domain.model.CashRegisterStatus;

public interface ICashRegisterPersistencePort {

    CashRegister save(CashRegister cashRegister);
    Boolean existsOpenCashRegisterByBusinessIdAndStatus(Long businessId, CashRegisterStatus status);

}
