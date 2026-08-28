package com.negocore.domain.spi;

import com.negocore.domain.model.Product;

public interface IProductPersistencePort {

    Product saveProduct(Product product);
    Boolean existByBusinessIdAndSku(Long businessId, String sku);
}
