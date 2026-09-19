package com.negocore.application.handler;

import com.negocore.application.dto.request.ProductRequestDTO;
import com.negocore.application.dto.request.ProductUpdateDTO;
import com.negocore.application.dto.request.StockPatchDTO;
import com.negocore.application.dto.response.ProductResponseDTO;
import com.negocore.application.mapper.IProductMapper;
import com.negocore.domain.api.IProductServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public ProductResponseDTO updateProduct(
            Long businessId,
            Long productId,
            ProductUpdateDTO productUpdateDTO
    ) {
        return mapper.toResponse(
                productServicePort.updateProduct(
                        businessId,
                        productId,
                        mapper.toDomain(productUpdateDTO)
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

    @Override
    public void deleteProduct(Long businessId, Long productId) {
        productServicePort.deleteProduct(businessId, productId);
    }

    @Override
    public ProductResponseDTO uploadProductImage(
            Long businessId,
            Long productId,
            MultipartFile file
    ) {
        byte[] content;
        try {
            content = file.getBytes();
        } catch (IOException e) {
            throw new BadRequestException(DomainConstants.IMAGE_FILE_READ_ERROR);
        }

        return mapper.toResponse(
                productServicePort.uploadProductImage(
                        businessId,
                        productId,
                        file.getContentType(),
                        file.getSize(),
                        content
                )
        );
    }
}