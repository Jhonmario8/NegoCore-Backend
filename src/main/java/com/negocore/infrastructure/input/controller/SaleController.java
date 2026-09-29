package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.DateUpdateDTO;
import com.negocore.application.dto.request.SaleRequestDTO;
import com.negocore.application.dto.response.SaleListResponseDTO;
import com.negocore.application.dto.response.SaleResponseDTO;
import com.negocore.application.handler.ISaleHandler;
import com.negocore.domain.model.SaleStatus;
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
public class SaleController {

    private final ISaleHandler saleHandler;

    @PostMapping("/{businessId}/sales")
    public ResponseEntity<SaleResponseDTO> registerSale(@PathVariable Long businessId, @Valid @RequestBody SaleRequestDTO saleRequestDTO) {
        SaleResponseDTO saleResponseDTO = saleHandler.registerSale(businessId, saleRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saleResponseDTO);
    }

    @PostMapping("/{businessId}/sales/{saleId}/cancel")
    public ResponseEntity<SaleResponseDTO> cancelSale(@PathVariable Long businessId, @PathVariable Long saleId) {
        SaleResponseDTO saleResponseDTO = saleHandler.cancelSale(businessId, saleId);
        return ResponseEntity.ok(saleResponseDTO);
    }

    @PatchMapping("/{businessId}/sales/{saleId}/date")
    public ResponseEntity<SaleResponseDTO> updateSaleDate(@PathVariable Long businessId, @PathVariable Long saleId, @Valid @RequestBody DateUpdateDTO dateUpdateDTO) {
        SaleResponseDTO saleResponseDTO = saleHandler.updateSaleDate(businessId, saleId, dateUpdateDTO);
        return ResponseEntity.ok(saleResponseDTO);
    }

    @GetMapping("/{businessId}/sales")
    public ResponseEntity<List<SaleListResponseDTO>> findSales(
            @PathVariable Long businessId,

            @RequestParam(required = false)
            SaleStatus status,

            @RequestParam(required = false)
            Long clientId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {
        return ResponseEntity.ok(
                saleHandler.findSales(
                        businessId,
                        status,
                        clientId,
                        from,
                        to
                )
        );
    }

    @GetMapping("/{businessId}/sales/{saleId}")
    public ResponseEntity<SaleResponseDTO> findSaleById(
            @PathVariable Long businessId,
            @PathVariable Long saleId
    ) {
        return ResponseEntity.ok(
                saleHandler.findSaleById(
                        businessId,
                        saleId
                )
        );
    }
}
