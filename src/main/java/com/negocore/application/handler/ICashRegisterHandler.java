package com.negocore.application.handler;

import com.negocore.application.dto.request.CashRegisterRequestDTO;
import com.negocore.application.dto.response.CashRegisterResponseDTO;

public interface ICashRegisterHandler {

    CashRegisterResponseDTO openCashRegister(Long businessId, CashRegisterRequestDTO cashRegisterRequestDTO);

}
