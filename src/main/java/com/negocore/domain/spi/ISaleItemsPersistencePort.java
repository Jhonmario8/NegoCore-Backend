package com.negocore.domain.spi;

import com.negocore.domain.model.SaleItem;

import java.util.List;

public interface ISaleItemsPersistencePort {

    void saveAll(List<SaleItem> saleItems);

}
