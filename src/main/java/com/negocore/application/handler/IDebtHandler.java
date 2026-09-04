package com.negocore.application.handler;

import com.negocore.application.dto.request.DebtCreateRequestDTO;
import com.negocore.application.dto.response.DebtResponseDTO;

public interface IDebtHandler {

    DebtResponseDTO createDebt(Long businessId, Long clientId, DebtCreateRequestDTO debtCreateRequestDTO);

}
