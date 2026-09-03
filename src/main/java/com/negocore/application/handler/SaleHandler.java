package com.negocore.application.handler;

import com.negocore.application.dto.request.SaleRequestDTO;
import com.negocore.application.dto.response.SaleResponseDTO;
import com.negocore.application.mapper.ISaleMapper;
import com.negocore.domain.api.ISaleServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SaleHandler implements ISaleHandler {

    private final ISaleServicePort saleServicePort;
    private final ISaleMapper saleMapper;


    @Override
    public SaleResponseDTO registerSale(Long businessId, SaleRequestDTO saleRequestDTO) {
        return saleMapper.toResponseDto(saleServicePort.registerSale(businessId, saleMapper.toDomain(saleRequestDTO)));
    }
}
