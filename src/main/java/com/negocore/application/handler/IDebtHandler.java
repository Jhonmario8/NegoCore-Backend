package com.negocore.application.handler;

import com.negocore.application.dto.request.DebtCreateRequestDTO;
import com.negocore.application.dto.request.LoanRequestDTO;
import com.negocore.application.dto.response.DebtListResponseDTO;
import com.negocore.application.dto.response.DebtPaymentDTO;
import com.negocore.application.dto.response.DebtResponseDTO;
import com.negocore.domain.model.DebtStatus;

import java.util.List;

public interface IDebtHandler {

    DebtResponseDTO createDebt(Long businessId, Long debtId, DebtCreateRequestDTO debtCreateRequestDTO);
    List<DebtListResponseDTO> findDebts(
            Long businessId,
            DebtStatus status,
            Long clientId
    );
    List<DebtListResponseDTO> findGroupedDebts(
            Long businessId,
            DebtStatus status,
            Long clientId
    );
    DebtListResponseDTO registerLoan(Long businessId, LoanRequestDTO loanRequestDTO);
    List<DebtPaymentDTO> findPaymentsByDebt(Long businessId, Long debtId);
    DebtResponseDTO updateDebtPayment(Long businessId, Long debtId, Long paymentId, DebtCreateRequestDTO debtCreateRequestDTO);
    List<DebtResponseDTO> payClientDebt(Long businessId, Long clientId, DebtCreateRequestDTO debtCreateRequestDTO);
    List<DebtPaymentDTO> findClientDebtPayments(Long businessId, Long clientId);
    DebtResponseDTO updateClientDebtPayment(Long businessId, Long clientId, Long paymentId, DebtCreateRequestDTO debtCreateRequestDTO);
}
