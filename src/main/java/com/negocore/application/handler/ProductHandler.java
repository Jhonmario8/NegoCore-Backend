package com.negocore.application.handler;

import com.negocore.application.dto.request.ProductRequestDTO;
import com.negocore.application.dto.request.StockPatchDTO;
import com.negocore.application.dto.response.ProductResponseDTO;
import com.negocore.application.mapper.IProductMapper;
import com.negocore.domain.api.IProductServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductHandler implements IProductHandler {

    private final IProductServicePort productServicePort;
    private final IProductMapper mapper;

    @Override
    public ProductResponseDTO createProduct(
            Long businessId,
            ProductRequestDTO productRequestDTO
    ) {
        return mapper.toResponse(
                productServicePort.createProduct(
                        businessId,
                        mapper.toDomain(productRequestDTO)
                )
        );
    }

    @Override
    public ProductResponseDTO updateStock(
            Long businessId,
            Long productId,
            StockPatchDTO stockPatchDTO
    ) {
        return mapper.toResponse(
                productServicePort.updateStock(
                        businessId,
                        productId,
                        stockPatchDTO.getQuantity(),
                        stockPatchDTO.getReason()
                )
        );
    }

    @Override
    public List<ProductResponseDTO> findProducts(
            Long businessId,
            Long categoryId,
            Boolean lowStock
    ) {
        return productServicePort.findProducts(
                        businessId,
                        categoryId,
                        lowStock
                )
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponseDTO findProductById(
            Long businessId,
            Long productId
    ) {
        return mapper.toResponse(
                productServicePort.findProductById(
                        businessId,
                        productId
                )
        );
    }
}