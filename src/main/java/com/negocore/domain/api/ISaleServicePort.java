package com.negocore.domain.api;

import com.negocore.domain.model.SaleRequest;
import com.negocore.domain.model.SaleResponse;

public interface ISaleServicePort {

    SaleResponse registerSale(Long businessId , SaleRequest saleRequest);


}
