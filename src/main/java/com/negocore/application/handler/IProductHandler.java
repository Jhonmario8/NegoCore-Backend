package com.negocore.application.handler;

import com.negocore.application.dto.request.ProductRequestDTO;
import com.negocore.application.dto.request.StockPatchDTO;
import com.negocore.application.dto.response.ProductResponseDTO;

import java.util.List;

public interface IProductHandler {

    ProductResponseDTO createProduct(Long businessId, ProductRequestDTO productRequestDTO);

    ProductResponseDTO updateStock(Long businessId, Long productId, StockPatchDTO stockPatchDTO);

    List<ProductResponseDTO> findProducts(
            Long businessId,
            Long categoryId,
            Boolean lowStock
    );

    ProductResponseDTO findProductById(Long businessId, Long productId);
}