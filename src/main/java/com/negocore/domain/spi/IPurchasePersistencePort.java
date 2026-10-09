package com.negocore.domain.spi;

import com.negocore.domain.model.Purchase;
import com.negocore.domain.model.PurchaseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IPurchasePersistencePort {

    Purchase savePurchase(Purchase purchase);

    Optional<Purchase> findByIdAndBusinessId(Long purchaseId, Long businessId);

    List<Purchase> findAllByFilters(
            Long businessId,
            Long providerId,
            PurchaseStatus status,
            LocalDateTime from,
            LocalDateTime to
    );

    BigDecimal sumTotalByBusinessIdAndCreatedAtBetweenAndStatusNot(
            Long businessId,
            LocalDateTime from,
            LocalDateTime to,
            PurchaseStatus status
    );
}
