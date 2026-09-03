package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.ISaleServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.ConflictException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.*;
import com.negocore.domain.spi.*;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;


@RequiredArgsConstructor
public class SaleService implements ISaleServicePort {

    private final ISalePersistencePort salePersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final ICashRegisterPersistencePort cashRegisterPersistencePort;
    private final IProductPersistencePort productPersistencePort;
    private final ISaleItemsPersistencePort saleItemsPersistencePort;
    private final ICashMovementPersistencePort cashMovementPersistencePort;
    private final IDebtPersistencePort debtPersistencePort;
    private final IDebtPaymentPersistencePort debtPaymentPersistencePort;

    @Override
    @Transactional
    public SaleResponse registerSale(Long businessId, SaleRequest saleRequest) {

        BigDecimal totalAmount = BigDecimal.ZERO;

        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }
        CashRegister cashRegister = cashRegisterPersistencePort.findOpenCashRegisterByBusinessIdAndStatus(businessId, CashRegisterStatus.OPEN)
                .orElseThrow(() -> new ConflictException(DomainConstants.CASH_REGISTER_NOT_OPEN));

        if (saleRequest.saleItems() == null || saleRequest.saleItems().isEmpty()) {
            throw new BadRequestException(DomainConstants.SALE_ITEMS_REQUIRED);
        }

        List<Long> productIds = saleRequest.saleItems().stream()
                .map(SaleItemRequest::productId)
                .toList();

        Set<Long> uniqueProductIds = new HashSet<>(productIds);

        if (uniqueProductIds.size() != productIds.size()) {
            throw new BadRequestException(
                    DomainConstants.DUPLICATE_PRODUCT
            );
        }

        List<Product> products = productPersistencePort.findAllByIdsAndBusinessId(productIds, businessId);

        if (products.size() != productIds.size()) {
            throw new NotFoundException(DomainConstants.PRODUCT_NOT_FOUND);
        }



        Map<Long, Product> productsById = products.stream()
                .collect(Collectors.toMap(
                        Product::getId,
                        Function.identity()
                ));

        for (SaleItemRequest item : saleRequest.saleItems()) {

            Product product = productsById.get(item.productId());

            if (product.getStock() < item.quantity()) {
                throw new ConflictException(
                        DomainConstants.INSUFFICIENT_STOCK
                );
            }

            BigDecimal subtotal = product.getSalePrice()
                    .multiply(BigDecimal.valueOf(item.quantity()));

            totalAmount = totalAmount.add(subtotal);
        }

        if (saleRequest.paidAmount() == null) {
            throw new BadRequestException(DomainConstants.INVALID_PAID_AMOUNT);
        }

        if (saleRequest.paidAmount().compareTo(BigDecimal.ZERO) < 0
                || saleRequest.paidAmount().compareTo(totalAmount) > 0) {
            throw new BadRequestException(DomainConstants.INVALID_PAID_AMOUNT);
        }

        SaleStatus status =
                saleRequest.paidAmount().compareTo(totalAmount) >= 0
                        ? SaleStatus.PAID
                        : SaleStatus.PARTIAL;

        if (status == SaleStatus.PARTIAL && saleRequest.clientId() == null) {
            throw new BadRequestException(
                    DomainConstants.CLIENT_REQUIRED_FOR_PARTIAL_PAYMENT
            );
        }



        for (SaleItemRequest item : saleRequest.saleItems()) {

            Product product = productsById.get(item.productId());

            product.setStock(product.getStock() - item.quantity());

            productPersistencePort.saveProduct(product);
        }



        Sale sale = new Sale();
        sale.setBusinessId(businessId);
        sale.setCashRegisterId(cashRegister.getId());
        sale.setClientId(saleRequest.clientId());
        sale.setTotal(totalAmount);
        sale.setStatus(status);
        sale.setPaidAmount(saleRequest.paidAmount());
        sale.setPaymentMethod(saleRequest.paymentMethod());
        sale.setCreatedAt(LocalDateTime.now());

        Sale savedSale = salePersistencePort.saveSale(sale);

        CashMovement cashMovement = new CashMovement();
        cashMovement.setCashRegisterId(cashRegister.getId());
        cashMovement.setType(CashMovementType.SALE);
        cashMovement.setAmount(saleRequest.paidAmount());
        cashMovement.setDescription(DomainConstants.SALE_CASH_MOVEMENT_DESCRIPTION + savedSale.getId());
        cashMovement.setReferenceId(savedSale.getId());
        cashMovement.setCreatedAt(LocalDateTime.now());

        cashMovementPersistencePort.save(cashMovement);

        if (status == SaleStatus.PARTIAL){
            Debt debt = new Debt();
            debt.setBusinessId(businessId);
            debt.setClientId(saleRequest.clientId());
            debt.setSaleId(savedSale.getId());
            debt.setTotalAmount(totalAmount);
            debt.setPaidAmount(saleRequest.paidAmount());
            debt.setStatus(DebtStatus.PARTIAL);
            LocalDateTime now = LocalDateTime.now();
            debt.setDueDate(now.plusDays(30).toLocalDate());
            debt.setCreatedAt(now);
            debtPersistencePort.save(debt);
        }
        List<SaleItem> saleItems = saleRequest.saleItems()
                .stream()
                .map(item -> {
                    Product product = productsById.get(item.productId());

                    SaleItem saleItem = new SaleItem();
                    saleItem.setSaleId(savedSale.getId());
                    saleItem.setProductId(product.getId());
                    saleItem.setQuantity(item.quantity());
                    saleItem.setUnitPrice(product.getSalePrice());
                    saleItem.setSubtotal(
                            product.getSalePrice()
                                    .multiply(BigDecimal.valueOf(item.quantity()))
                    );

                    return saleItem;
                })
                .toList();
        saleItemsPersistencePort.saveAll(saleItems);

        return new SaleResponse(
                savedSale,
                saleItems
        );

    }

    @Override
    @Transactional
    public SaleResponse cancelSale(Long businessId, Long saleId) {

        Long userId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Sale sale = salePersistencePort.findById(saleId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.SALE_NOT_FOUND));

        if (!sale.getBusinessId().equals(businessId)) {
            throw new NotFoundException(DomainConstants.SALE_NOT_FOUND);
        }

        if (sale.getStatus() != SaleStatus.PAID && sale.getStatus() != SaleStatus.PARTIAL) {
            throw new ConflictException(DomainConstants.SALE_ALREADY_CANCELED);
        }

        List<SaleItem> saleItems = saleItemsPersistencePort.findAllBySaleId(saleId);

        Debt debt = debtPersistencePort.findBySaleId(saleId).orElse(null);

        if (debt != null && debtPaymentPersistencePort.existsByDebtId(debt.getId())) {
            throw new ConflictException(DomainConstants.SALE_CANNOT_BE_CANCELED_WITH_PAYMENTS);
        }

        List<Long> productIds = saleItems.stream()
                .map(SaleItem::getProductId)
                .toList();

        List<Product> products = productPersistencePort.findAllByIds(productIds);

        Map<Long, Product> productsById = products.stream()
                .collect(Collectors.toMap(
                        Product::getId,
                        Function.identity()
                ));

        for (SaleItem saleItem : saleItems) {

            Product product = productsById.get(saleItem.getProductId());

            product.setStock(product.getStock() + saleItem.getQuantity());
        }

        productPersistencePort.saveAll(products);


        if (debt != null) {
            debt.setStatus(DebtStatus.CANCELLED);
            debtPersistencePort.save(debt);
        }


        sale.setStatus(SaleStatus.CANCELLED);
        Sale savedSale = salePersistencePort.saveSale(sale);

        return new SaleResponse(
                savedSale,
                saleItems
        );
    }
}