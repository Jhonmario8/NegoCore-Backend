package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.DateUpdateDTO;
import com.negocore.application.dto.request.PurchaseRequestDTO;
import com.negocore.application.dto.response.PurchaseListResponseDTO;
import com.negocore.application.dto.response.PurchaseResponseDTO;
import com.negocore.application.handler.IPurchaseHandler;
import com.negocore.domain.model.PurchaseStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/businesses")
@RequiredArgsConstructor
public class PurchaseController {

    private final IPurchaseHandler purchaseHandler;

    @PostMapping("/{businessId}/purchases")
    public ResponseEntity<PurchaseResponseDTO> registerPurchase(
            @PathVariable Long businessId,
            @Valid @RequestBody PurchaseRequestDTO purchaseRequestDTO
    ) {
        PurchaseResponseDTO purchaseResponseDTO = purchaseHandler.registerPurchase(businessId, purchaseRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseResponseDTO);
    }

    @PatchMapping("/{businessId}/purchases/{purchaseId}/date")
    public ResponseEntity<PurchaseResponseDTO> updatePurchaseDate(
            @PathVariable Long businessId,
            @PathVariable Long purchaseId,
            @Valid @RequestBody DateUpdateDTO dateUpdateDTO
    ) {
        PurchaseResponseDTO purchaseResponseDTO = purchaseHandler.updatePurchaseDate(businessId, purchaseId, dateUpdateDTO);
        return ResponseEntity.ok(purchaseResponseDTO);
    }

    @GetMapping("/{businessId}/purchases")
    public ResponseEntity<List<PurchaseListResponseDTO>> findPurchases(
            @PathVariable Long businessId,

            @RequestParam(required = false)
            Long providerId,

            @RequestParam(required = false)
            PurchaseStatus status,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {
        return ResponseEntity.ok(
                purchaseHandler.findPurchases(
                        businessId,
                        providerId,
                        status,
                        from,
                        to
                )
        );
    }

    @GetMapping("/{businessId}/purchases/{purchaseId}")
    public ResponseEntity<PurchaseResponseDTO> findPurchaseById(
            @PathVariable Long businessId,
            @PathVariable Long purchaseId
    ) {
        return ResponseEntity.ok(
                purchaseHandler.findPurchaseById(businessId, purchaseId)
        );
    }
}
