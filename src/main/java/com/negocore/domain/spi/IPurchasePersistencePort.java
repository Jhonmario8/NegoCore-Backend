package com.negocore.domain.spi;

import com.negocore.domain.model.Purchase;
import com.negocore.domain.model.PurchaseStatus;

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
}
