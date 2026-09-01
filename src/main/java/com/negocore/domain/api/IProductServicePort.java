package com.negocore.domain.api;

import com.negocore.domain.model.Product;

public interface IProductServicePort {

    Product createProduct(Long businessId ,Product product);
    Product updateStock(Long businessId, Long productId, int quantity, String reason);

}
