package com.negocore.application.handler;

import com.negocore.application.dto.request.SaleRequestDTO;
import com.negocore.application.dto.response.SaleListResponseDTO;
import com.negocore.application.dto.response.SaleResponseDTO;
import com.negocore.domain.model.SaleStatus;

import java.time.LocalDate;
import java.util.List;

public interface ISaleHandler {

    SaleResponseDTO registerSale(
            Long businessId,
            SaleRequestDTO saleRequestDTO
    );

    SaleResponseDTO cancelSale(
            Long businessId,
            Long saleId
    );

    List<SaleListResponseDTO> findSales(
            Long businessId,
            SaleStatus status,
            Long clientId,
            LocalDate from,
            LocalDate to
    );

    SaleResponseDTO findSaleById(
            Long businessId,
            Long saleId
    );
}