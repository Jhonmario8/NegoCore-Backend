package com.negocore.application.handler;

import com.negocore.application.dto.request.CashRegisterCloseRequestDTO;
import com.negocore.application.dto.request.CashRegisterOpenRequestDTO;
import com.negocore.application.dto.response.CashRegisterClosedResponseDTO;
import com.negocore.application.dto.response.CashRegisterResponseDTO;

public interface ICashRegisterHandler {

    CashRegisterResponseDTO openCashRegister(Long businessId, CashRegisterOpenRequestDTO cashRegisterRequestDTO);
    CashRegisterClosedResponseDTO closeCashRegister(Long businessId, Long cashRegisterId, CashRegisterCloseRequestDTO cashRegisterCloseRequestDTO);
}
