package com.negocore.application.handler;

import com.negocore.application.dto.request.SaleRequestDTO;
import com.negocore.application.dto.response.SaleListResponseDTO;
import com.negocore.application.dto.response.SaleResponseDTO;
import com.negocore.application.mapper.ISaleMapper;
import com.negocore.domain.api.ISaleServicePort;
import com.negocore.domain.model.SaleStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleHandler implements ISaleHandler {

    private final ISaleServicePort saleServicePort;
    private final ISaleMapper saleMapper;

    @Override
    public SaleResponseDTO registerSale(
            Long businessId,
            SaleRequestDTO saleRequestDTO
    ) {
        return saleMapper.toResponseDto(
                saleServicePort.registerSale(
                        businessId,
                        saleMapper.toDomain(saleRequestDTO)
                )
        );
    }

    @Override
    public SaleResponseDTO cancelSale(
            Long businessId,
            Long saleId
    ) {
        return saleMapper.toResponseDto(
                saleServicePort.cancelSale(
                        businessId,
                        saleId
                )
        );
    }

    @Override
    public List<SaleListResponseDTO> findSales(
            Long businessId,
            SaleStatus status,
            Long clientId,
            LocalDate from,
            LocalDate to
    ) {
        LocalDateTime fromDateTime =
                from != null
                        ? from.atStartOfDay()
                        : null;

        LocalDateTime toDateTime =
                to != null
                        ? to.plusDays(1).atStartOfDay()
                        : null;

        return saleServicePort.findSales(
                        businessId,
                        status,
                        clientId,
                        fromDateTime,
                        toDateTime
                )
                .stream()
                .map(saleMapper::toListResponseDto)
                .toList();
    }

    @Override
    public SaleResponseDTO findSaleById(
            Long businessId,
            Long saleId
    ) {
        return saleMapper.toResponseDto(
                saleServicePort.findSaleById(
                        businessId,
                        saleId
                )
        );
    }
}