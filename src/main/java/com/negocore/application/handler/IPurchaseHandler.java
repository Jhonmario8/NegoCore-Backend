package com.negocore.application.handler;

import com.negocore.application.dto.request.DateUpdateDTO;
import com.negocore.application.dto.request.PurchaseRequestDTO;
import com.negocore.application.dto.response.PurchaseListResponseDTO;
import com.negocore.application.dto.response.PurchaseResponseDTO;
import com.negocore.domain.model.PurchaseStatus;

import java.time.LocalDate;
import java.util.List;

public interface IPurchaseHandler {

    PurchaseResponseDTO registerPurchase(Long businessId, PurchaseRequestDTO purchaseRequestDTO);

    PurchaseResponseDTO updatePurchaseDate(Long businessId, Long purchaseId, DateUpdateDTO dateUpdateDTO);

    List<PurchaseListResponseDTO> findPurchases(
            Long businessId,
            Long providerId,
            PurchaseStatus status,
            LocalDate from,
            LocalDate to
    );

    PurchaseResponseDTO findPurchaseById(Long businessId, Long purchaseId);
}
