package com.negocore.application.handler;

import com.negocore.application.dto.request.ProductRequestDTO;
import com.negocore.application.dto.request.StockPatchDTO;
import com.negocore.application.dto.response.ProductResponseDTO;

public interface IProductHandler {

    ProductResponseDTO createProduct(Long businessId ,ProductRequestDTO productRequestDTO);
    ProductResponseDTO updateStock(Long businessId, Long productId, StockPatchDTO stockPatchDTO);
}
