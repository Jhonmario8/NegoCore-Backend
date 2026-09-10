package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.*;
import com.negocore.application.dto.response.*;
import com.negocore.application.handler.*;
import com.negocore.domain.model.DebtStatus;
import com.negocore.domain.model.SaleStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    private final IBalanceReportHandler balanceReportHandler;
    private final IAuditLogHandler auditLogHandler;
    private final IProviderHandler providerHandler;

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

    @PostMapping("/{businessId}/debts/{debtId}/payments")
    public ResponseEntity<DebtResponseDTO> createDebtPayment(
            @PathVariable Long businessId,
            @PathVariable Long debtId,
            @Valid @RequestBody DebtCreateRequestDTO debtCreateRequestDTO) {
        DebtResponseDTO debtResponseDTO = debtHandler.createDebt(businessId, debtId, debtCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(debtResponseDTO);
    }

    @GetMapping("/{businessId}/reports/balance")
    public ResponseEntity<BalanceReportResponseDTO> getBalanceReport(
            @PathVariable Long businessId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {

        return ResponseEntity.ok(
                balanceReportHandler.getBalanceReport(
                        businessId,
                        from,
                        to
                )
        );
    }
    @GetMapping("/{businessId}/audit-logs")
    public ResponseEntity<AuditLogPageResponseDTO> findAuditLogs(
            @PathVariable Long businessId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @RequestParam(required = false)
            String action,

            @RequestParam(required = false)
            String entity,

            @RequestParam(required = false)
            Long entityId,

            @RequestParam(required = false)
            Integer page,

            @RequestParam(required = false)
            Integer size
    ) {

        return ResponseEntity.ok(
                auditLogHandler.findAuditLogs(
                        businessId,
                        from,
                        to,
                        action,
                        entity,
                        entityId,
                        page,
                        size
                )
        );
    }

    @GetMapping("/{businessId}/categories")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByBusinessId(@PathVariable Long businessId) {
        return ResponseEntity.ok(categoryHandler.getCategoriesByBusinessId(businessId));
    }

    @GetMapping("/{businessId}/products")
    public ResponseEntity<List<ProductResponseDTO>> findProducts(
            @PathVariable Long businessId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean lowStock
    ) {
        return ResponseEntity.ok(
                productHandler.findProducts(
                        businessId,
                        categoryId,
                        lowStock
                )
        );
    }

    @GetMapping("/{businessId}/products/{productId}")
    public ResponseEntity<ProductResponseDTO> findProductById(
            @PathVariable Long businessId,
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                productHandler.findProductById(
                        businessId,
                        productId
                )
        );
    }

    @GetMapping("/{businessId}/clients")
    public ResponseEntity<List<ClientResponseDTO>> getClientsByBusinessId(@PathVariable Long businessId) {
        return ResponseEntity.ok(clientHandler.getClientsByBusinessId(businessId));
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

    @GetMapping("/{businessId}/cash-registers/current")
    public ResponseEntity<CashRegisterResponseDTO> findCurrentCashRegister(
            @PathVariable Long businessId
    ) {
        return ResponseEntity.ok(
                cashRegisterHandler.findCurrentCashRegister(businessId)
        );
    }

    @GetMapping("/{businessId}/cash-registers/{cashRegisterId}/cash-movements")
    public ResponseEntity<List<CashMovementResponseDTO>> findCashMovementsByCashRegisterId(
            @PathVariable Long businessId,
            @PathVariable Long cashRegisterId
    ) {
        return ResponseEntity.ok(
                cashRegisterHandler.findCashMovementsByCashRegisterId(businessId, cashRegisterId)
        );
    }

    @GetMapping("/{businessId}/expenses")
    public ResponseEntity<List<ExpenseResponseDTO>> findExpenses(
            @PathVariable Long businessId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {

        return ResponseEntity.ok(
                expenseHandler.findExpenses(
                        businessId,
                        from,
                        to
                )
        );
    }

    @GetMapping("/{businessId}/debts")
    public ResponseEntity<List<DebtListResponseDTO>> findDebts(
            @PathVariable Long businessId,
            @RequestParam(required = false) DebtStatus status,
            @RequestParam(required = false) Long clientId
    ) {

        return ResponseEntity.ok(
                debtHandler.findDebts(
                        businessId,
                        status,
                        clientId
                )
        );
    }

    @PostMapping("/{businessId}/providers")
    public ResponseEntity<ProviderResponseDTO> createProvider(
            @PathVariable Long businessId,
            @Valid @RequestBody ProviderCreateRequestDTO providerCreateRequestDTO
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        providerHandler.createProvider(
                                businessId,
                                providerCreateRequestDTO
                        )
                );
    }

    @GetMapping("/{businessId}/providers")
    public ResponseEntity<List<ProviderResponseDTO>> findAllByBusinessId(
            @PathVariable Long businessId
    ) {

        return ResponseEntity.ok(
                providerHandler.findAllByBusinessId(businessId)
        );
    }

}
