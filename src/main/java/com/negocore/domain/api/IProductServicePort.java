package com.negocore.domain.api;

import com.negocore.domain.model.Product;

import java.util.List;

public interface IProductServicePort {

    Product createProduct(Long businessId, Product product);

    Product updateStock(Long businessId, Long productId, int quantity, String reason);

    Product updateProduct(Long businessId, Long productId, Product productChanges);

    List<Product> findProducts(Long businessId, Long categoryId, Boolean lowStock);

    Product findProductById(Long businessId, Long productId);

    void deleteProduct(Long businessId, Long productId);

    Product uploadProductImage(
            Long businessId,
            Long productId,
            String contentType,
            long size,
            byte[] content
    );
}
