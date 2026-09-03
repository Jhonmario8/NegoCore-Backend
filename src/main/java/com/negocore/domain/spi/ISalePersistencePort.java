package com.negocore.domain.spi;

import com.negocore.domain.model.Sale;
public interface ISalePersistencePort {

    Sale saveSale(Sale sale);



}
