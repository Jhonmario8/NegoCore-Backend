package com.negocore.domain.api;

import com.negocore.domain.model.CashRegister;

import java.math.BigDecimal;

public interface ICashRegisterServicePort {

    CashRegister openCashRegister(Long businessId, BigDecimal openingAmount);

}
