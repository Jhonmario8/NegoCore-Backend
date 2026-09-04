package com.negocore.domain.api;


import com.negocore.domain.model.DebtCreateRequest;

import com.negocore.domain.model.DebtPaymentResponse;

public interface IDebtServicePort {

    DebtPaymentResponse createDebt(Long businessId, Long debtId, DebtCreateRequest debtCreateRequest);

}
