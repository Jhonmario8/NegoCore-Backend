package com.negocore.domain.spi;

import com.negocore.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface IProductPersistencePort {

    Product saveProduct(Product product);
    Boolean existByBusinessIdAndSku(Long businessId, String sku);
    Optional<Product> findById(Long productId);
    List<Product> findAllByIdsAndBusinessId(List<Long> productIds, Long businessId);
}
