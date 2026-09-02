package com.negocore.application.handler;

import com.negocore.application.dto.request.CashRegisterRequestDTO;
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
    public CashRegisterResponseDTO openCashRegister(Long businessId, CashRegisterRequestDTO cashRegisterRequestDTO) {
        return mapper.toResponseDTO(cashRegisterServicePort.openCashRegister(businessId, cashRegisterRequestDTO.getOpeningAmount()));
    }
}
