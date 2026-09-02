package com.negocore.domain.api;

import com.negocore.domain.model.CashRegister;

public interface ICashRegisterServicePort {

    CashRegister openCashRegister(Long businessId, Double openingAmount);

}
