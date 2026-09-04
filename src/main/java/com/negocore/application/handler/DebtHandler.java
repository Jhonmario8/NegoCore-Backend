package com.negocore.application.handler;

import com.negocore.application.dto.request.DebtCreateRequestDTO;
import com.negocore.application.dto.response.DebtResponseDTO;
import com.negocore.application.mapper.IDebtMapper;
import com.negocore.domain.api.IDebtServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DebtHandler implements IDebtHandler {

    private final IDebtMapper debtMapper;
    private final IDebtServicePort debtService;

    @Override
    public DebtResponseDTO createDebt(Long businessId, Long clientId, DebtCreateRequestDTO debtCreateRequestDTO) {
        return debtMapper.toResponseDTO(debtService.createDebt(businessId, clientId, debtMapper.toCreateRequest(debtCreateRequestDTO)));
    }
}
