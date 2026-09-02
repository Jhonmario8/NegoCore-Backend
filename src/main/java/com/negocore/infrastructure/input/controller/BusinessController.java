package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.*;
import com.negocore.application.dto.response.*;
import com.negocore.application.handler.IBusinessHandler;
import com.negocore.application.handler.ICashRegisterHandler;
import com.negocore.application.handler.ICategoryHandler;
import com.negocore.application.handler.IProductHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final IBusinessHandler businessHandler;
    private final ICategoryHandler categoryHandler;
    private final IProductHandler productHandler;
    private final ICashRegisterHandler cashRegisterHandler;

    @PostMapping()
    public ResponseEntity<BusinessResponseDTO> createBusiness(@Valid @RequestBody BusinessCreateDTO businessCreateDTO) {
        BusinessResponseDTO businessResponseDTO = businessHandler.createBusiness(businessCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(businessResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<BusinessListResponseDTO>> findAllBusinesses() {
        List<BusinessListResponseDTO> businesses = businessHandler.findAllBusiness();
        return ResponseEntity.ok(businesses);
    }

    @PostMapping("/{businessId}/categories")
    public ResponseEntity<CategoryResponseDTO> createCategory(@PathVariable Long businessId, @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        CategoryResponseDTO categoryResponseDTO = categoryHandler.createCategory(businessId, categoryRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponseDTO);
    }

    @PostMapping("/{businessId}/products")
    public ResponseEntity<ProductResponseDTO> createProduct(@PathVariable Long businessId, @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO productResponseDTO = productHandler.createProduct(businessId, productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDTO);
    }

    @PatchMapping("/{businessId}/products/{productId}/stock")
    public ResponseEntity<ProductResponseDTO> updateStock(@PathVariable Long businessId, @PathVariable Long productId,@Valid @RequestBody StockPatchDTO stockPatchDTO) {
        ProductResponseDTO productResponseDTO = productHandler.updateStock(businessId, productId, stockPatchDTO);
        return ResponseEntity.ok(productResponseDTO);
    }

    @PostMapping("/{businessId}/cash-registers")
    public ResponseEntity<CashRegisterResponseDTO> openCashRegister(@PathVariable Long businessId, @Valid @RequestBody CashRegisterRequestDTO cashRegisterRequestDTO) {
        CashRegisterResponseDTO cashRegisterResponseDTO = cashRegisterHandler.openCashRegister(businessId, cashRegisterRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cashRegisterResponseDTO);
    }

}
