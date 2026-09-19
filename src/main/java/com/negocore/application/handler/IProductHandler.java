package com.negocore.application.handler;

import com.negocore.application.dto.request.ProductRequestDTO;
import com.negocore.application.dto.request.ProductUpdateDTO;
import com.negocore.application.dto.request.StockPatchDTO;
import com.negocore.application.dto.response.ProductResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductHandler {

    ProductResponseDTO createProduct(Long businessId, ProductRequestDTO productRequestDTO);

    ProductResponseDTO updateStock(Long businessId, Long productId, StockPatchDTO stockPatchDTO);

    ProductResponseDTO updateProduct(Long businessId, Long productId, ProductUpdateDTO productUpdateDTO);

    List<ProductResponseDTO> findProducts(
            Long businessId,
            Long categoryId,
            Boolean lowStock
    );

    ProductResponseDTO findProductById(Long businessId, Long productId);

    ProductResponseDTO uploadProductImage(Long businessId, Long productId, MultipartFile file);
}