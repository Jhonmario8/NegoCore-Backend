package com.negocore.domain.api;

import com.negocore.domain.model.Purchase;
import com.negocore.domain.model.PurchaseRequest;
import com.negocore.domain.model.PurchaseResponse;
import com.negocore.domain.model.PurchaseStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface IPurchaseServicePort {

    PurchaseResponse registerPurchase(Long businessId, PurchaseRequest purchaseRequest);

    PurchaseResponse updatePurchaseDate(Long businessId, Long purchaseId, LocalDateTime createdAt);

    List<Purchase> findPurchases(
            Long businessId,
            Long providerId,
            PurchaseStatus status,
            LocalDateTime from,
            LocalDateTime to
    );

    PurchaseResponse findPurchaseById(Long businessId, Long purchaseId);
}
