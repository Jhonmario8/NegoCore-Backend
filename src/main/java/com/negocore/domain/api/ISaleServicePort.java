package com.negocore.domain.api;

import com.negocore.domain.model.Sale;
import com.negocore.domain.model.SaleRequest;
import com.negocore.domain.model.SaleResponse;
import com.negocore.domain.model.SaleStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface ISaleServicePort {

    SaleResponse registerSale(Long businessId , SaleRequest saleRequest);
    SaleResponse cancelSale(Long businessId, Long saleId);
    List<Sale> findSales(
            Long businessId,
            SaleStatus status,
            Long clientId,
            LocalDateTime from,
            LocalDateTime to
    );

    SaleResponse findSaleById(Long businessId, Long saleId);
}
