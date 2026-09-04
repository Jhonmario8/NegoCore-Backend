package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.*;
import com.negocore.application.dto.response.*;
import com.negocore.application.handler.*;
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
    private final ISaleHandler saleHandler;
    private final IExpenseHandler expenseHandler;
    private final IClientHandler clientHandler;
    private final IDebtHandler debtHandler;

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
    public ResponseEntity<ProductResponseDTO> updateStock(@PathVariable Long businessId, @PathVariable Long productId, @Valid @RequestBody StockPatchDTO stockPatchDTO) {
        ProductResponseDTO productResponseDTO = productHandler.updateStock(businessId, productId, stockPatchDTO);
        return ResponseEntity.ok(productResponseDTO);
    }

    @PostMapping("/{businessId}/cash-registers")
    public ResponseEntity<CashRegisterResponseDTO> openCashRegister(@PathVariable Long businessId, @Valid @RequestBody CashRegisterOpenRequestDTO cashRegisterRequestDTO) {
        CashRegisterResponseDTO cashRegisterResponseDTO = cashRegisterHandler.openCashRegister(businessId, cashRegisterRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cashRegisterResponseDTO);
    }

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

    @PostMapping("/{businessId}/expenses")
    public ResponseEntity<ExpenseResponseDTO> registerExpense(@PathVariable Long businessId, @Valid @RequestBody ExpenseRequestDTO expenseRequestDTO) {
        ExpenseResponseDTO expenseResponseDTO = expenseHandler.registerExpense(businessId, expenseRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseResponseDTO);
    }

    @PostMapping("/{businessId}/cash-registers/{cashRegisterId}/close")
    public ResponseEntity<CashRegisterClosedResponseDTO> closeCashRegister(@PathVariable Long businessId,
                                                                           @PathVariable Long cashRegisterId,
                                                                           @Valid @RequestBody CashRegisterCloseRequestDTO cashRegisterCloseRequestDTO) {
        CashRegisterClosedResponseDTO cashRegisterClosedResponseDTO = cashRegisterHandler.closeCashRegister(businessId, cashRegisterId, cashRegisterCloseRequestDTO);
        return ResponseEntity.ok(cashRegisterClosedResponseDTO);
    }

    @PostMapping("/{businessId}/clients")
    public ResponseEntity<ClientResponseDTO> registerClient(@PathVariable Long businessId, @Valid @RequestBody ClientRequestDTO clientRequestDTO) {
        ClientResponseDTO clientResponseDTO = clientHandler.registerClient(businessId, clientRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientResponseDTO);
    }

    @PostMapping("/{businessId}/clients/{clientId}/payments")
    public ResponseEntity<DebtResponseDTO> createDebt(@PathVariable Long businessId, @PathVariable Long clientId, @Valid @RequestBody DebtCreateRequestDTO debtCreateRequestDTO) {
        DebtResponseDTO debtResponseDTO = debtHandler.createDebt(businessId, clientId, debtCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(debtResponseDTO);
    }
}
