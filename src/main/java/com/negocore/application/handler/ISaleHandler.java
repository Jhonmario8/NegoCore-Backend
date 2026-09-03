package com.negocore.application.handler;

import com.negocore.application.dto.request.SaleRequestDTO;
import com.negocore.application.dto.response.SaleResponseDTO;

public interface ISaleHandler {

    SaleResponseDTO registerSale(Long businessId, SaleRequestDTO saleRequestDTO);
    SaleResponseDTO cancelSale(Long businessId, Long saleId);
}
