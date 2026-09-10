package com.negocore.application.handler;

import com.negocore.application.dto.request.DebtCreateRequestDTO;
import com.negocore.application.dto.response.DebtListResponseDTO;
import com.negocore.application.dto.response.DebtResponseDTO;
import com.negocore.application.mapper.IDebtMapper;
import com.negocore.domain.api.IDebtServicePort;
import com.negocore.domain.model.DebtStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DebtHandler implements IDebtHandler {

    private final IDebtMapper debtMapper;
    private final IDebtServicePort debtService;

    @Override
    public DebtResponseDTO createDebt(Long businessId, Long debtId, DebtCreateRequestDTO debtCreateRequestDTO) {
        return debtMapper.toResponseDTO(debtService.createDebt(businessId, debtId, debtMapper.toCreateRequest(debtCreateRequestDTO)));
    }

    @Override
    public List<DebtListResponseDTO> findDebts(
            Long businessId,
            DebtStatus status,
            Long clientId
    ) {

        return debtService.findDebts(
                        businessId,
                        status,
                        clientId
                )
                .stream()
                .map(debtMapper::toListResponseDTO)
                .toList();
    }
}
