package com.negocore.domain.api;

import com.negocore.domain.model.CashRegister;
import com.negocore.domain.model.CashRegisterResponse;

import java.math.BigDecimal;

public interface ICashRegisterServicePort {

    CashRegister openCashRegister(Long businessId, BigDecimal openingAmount);
    CashRegisterResponse closeCashRegister(Long businessId, Long cashRegisterId, BigDecimal closingAmount);
}
