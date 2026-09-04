package com.negocore.application.handler;

import com.negocore.application.dto.request.CashRegisterCloseRequestDTO;
import com.negocore.application.dto.request.CashRegisterOpenRequestDTO;
import com.negocore.application.dto.response.CashRegisterClosedResponseDTO;
import com.negocore.application.dto.response.CashRegisterResponseDTO;
import com.negocore.application.mapper.ICashRegisterMapper;
import com.negocore.domain.api.ICashRegisterServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CashRegisterHandler implements ICashRegisterHandler {

    private final ICashRegisterServicePort cashRegisterServicePort;
    private final ICashRegisterMapper mapper;

    @Override
    public CashRegisterResponseDTO openCashRegister(Long businessId, CashRegisterOpenRequestDTO cashRegisterRequestDTO) {
        return mapper.toResponseDTO(cashRegisterServicePort.openCashRegister(businessId, cashRegisterRequestDTO.getOpeningAmount()));
    }

    @Override
    public CashRegisterClosedResponseDTO closeCashRegister(Long businessId, Long cashRegisterId, CashRegisterCloseRequestDTO cashRegisterCloseRequestDTO) {
        return mapper.toClosedDto(cashRegisterServicePort.closeCashRegister(businessId, cashRegisterId, cashRegisterCloseRequestDTO.getClosingAmount()));
    }
}
