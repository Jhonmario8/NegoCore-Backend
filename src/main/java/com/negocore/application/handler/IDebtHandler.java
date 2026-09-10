package com.negocore.application.handler;

import com.negocore.application.dto.request.DebtCreateRequestDTO;
import com.negocore.application.dto.response.DebtListResponseDTO;
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
}
