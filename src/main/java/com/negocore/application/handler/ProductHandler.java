package com.negocore.application.handler;

import com.negocore.application.dto.request.ProductRequestDTO;
import com.negocore.application.dto.request.StockPatchDTO;
import com.negocore.application.dto.response.ProductResponseDTO;
import com.negocore.application.mapper.IProductMapper;
import com.negocore.domain.api.IProductServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductHandler implements IProductHandler{

    private final IProductServicePort productServicePort;
    private final IProductMapper mapper;

    @Override
    public ProductResponseDTO createProduct(Long businessId, ProductRequestDTO productRequestDTO) {
        return mapper.toResponse(productServicePort.createProduct(businessId, mapper.toDomain(productRequestDTO)));
    }

    @Override
    public ProductResponseDTO updateStock(Long businessId, Long productId, StockPatchDTO stockPatchDTO) {
        return mapper.toResponse(productServicePort.updateStock(businessId, productId, stockPatchDTO.getQuantity(), stockPatchDTO.getReason()));
    }
}
