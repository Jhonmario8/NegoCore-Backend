package com.negocore.domain.api;


import com.negocore.domain.model.Debt;
import com.negocore.domain.model.DebtCreateRequest;

import com.negocore.domain.model.DebtPayment;
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
    List<Debt> findGroupedDebts(Long businessId, DebtStatus status, Long clientId);
    Debt registerLoan(Long businessId, Debt loan);
    List<DebtPayment> findPaymentsByDebt(Long businessId, Long debtId);
    DebtPaymentResponse updateDebtPayment(Long businessId, Long debtId, Long paymentId, DebtCreateRequest updateRequest);
    List<DebtPaymentResponse> payClientDebt(Long businessId, Long clientId, DebtCreateRequest request);
    List<DebtPayment> findClientDebtPayments(Long businessId, Long clientId);
    DebtPaymentResponse updateClientDebtPayment(Long businessId, Long clientId, Long paymentId, DebtCreateRequest updateRequest);
}
