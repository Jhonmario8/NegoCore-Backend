package com.negocore.domain.api;


import com.negocore.domain.model.Debt;
import com.negocore.domain.model.DebtCreateRequest;

import com.negocore.domain.model.DebtPaymentResponse;
import com.negocore.domain.model.DebtStatus;

import java.util.List;

public interface IDebtServicePort {

    DebtPaymentResponse createDebt(Long businessId, Long debtId, DebtCreateRequest debtCreateRequest);
    List<Debt> findDebts(
            Long businessId,
            DebtStatus status,
            Long clientId
    );
}
