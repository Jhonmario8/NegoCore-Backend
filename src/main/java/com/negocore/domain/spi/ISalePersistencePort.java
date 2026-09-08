package com.negocore.domain.spi;

import com.negocore.domain.model.Sale;
import com.negocore.domain.model.SaleStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

public interface ISalePersistencePort {

    Sale saveSale(Sale sale);

    Optional<Sale> findById(Long saleId);

    BigDecimal sumTotalByBusinessIdAndCreatedAtBetweenAndStatusNot(
            Long businessId,
            LocalDateTime from,
            LocalDateTime to,
            SaleStatus status
    );

    Long countByBusinessIdAndCreatedAtBetweenAndStatusNot(
            Long businessId,
            LocalDateTime from,
            LocalDateTime to,
            SaleStatus status
    );

}