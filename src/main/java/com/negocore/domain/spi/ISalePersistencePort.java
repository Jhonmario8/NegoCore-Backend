package com.negocore.domain.spi;

import com.negocore.domain.model.Sale;

import java.util.Optional;

public interface ISalePersistencePort {

    Sale saveSale(Sale sale);
    Optional<Sale> findById(Long saleId);


}
