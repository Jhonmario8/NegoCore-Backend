package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.ISaleServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.ConflictException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.*;
import com.negocore.domain.spi.*;

import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    private final IProductPersistencePort productPersistencePort;
    private final ISaleItemsPersistencePort saleItemsPersistencePort;
    private final IDebtPersistencePort debtPersistencePort;
    private final IDebtPaymentPersistencePort debtPaymentPersistencePort;
    private final IAuditLogsPersistencePort auditLogsPersistencePort;

    @Override
    public SaleResponse registerSale(Long businessId, SaleRequest saleRequest) {

        BigDecimal totalAmount = BigDecimal.ZERO;

        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

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

            BigDecimal unitPrice = item.unitPrice() != null ? item.unitPrice() : product.getSalePrice();
            BigDecimal subtotal = unitPrice
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
        sale.setClientId(saleRequest.clientId());
        sale.setTotal(totalAmount);
        sale.setStatus(status);
        sale.setPaidAmount(saleRequest.paidAmount());
        sale.setPaymentMethod(saleRequest.paymentMethod());
        sale.setCreatedAt(saleRequest.createdAt() != null ? saleRequest.createdAt() : LocalDateTime.now());

        Sale savedSale = salePersistencePort.saveSale(sale);


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
                    BigDecimal unitPrice = item.unitPrice() != null ? item.unitPrice() : product.getSalePrice();

                    SaleItem saleItem = new SaleItem();
                    saleItem.setSaleId(savedSale.getId());
                    saleItem.setProductId(product.getId());
                    saleItem.setQuantity(item.quantity());
                    saleItem.setUnitPrice(unitPrice);
                    saleItem.setSubtotal(
                            unitPrice.multiply(BigDecimal.valueOf(item.quantity()))
                    );

                    return saleItem;
                })
                .toList();
        saleItemsPersistencePort.saveAll(saleItems);

        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessId(businessId);
        auditLog.setUserId(userId);
        auditLog.setAction(DomainConstants.SALE_CREATED);
        auditLog.setEntity(DomainConstants.SALE_ENTITY);
        auditLog.setEntityId(savedSale.getId());
        auditLog.setDetails(DomainConstants.SALE_CREATED_DETAILS + savedSale.getId());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogsPersistencePort.save(auditLog);

        return new SaleResponse(
                savedSale,
                saleItems
        );

    }

    @Override
    public SaleResponse updateSale(Long businessId, Long saleId, SaleRequest saleRequest) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Sale sale = salePersistencePort.findByIdAndBusinessId(saleId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.SALE_NOT_FOUND));

        if (sale.getStatus() == SaleStatus.CANCELLED) {
            throw new ConflictException(DomainConstants.SALE_ALREADY_CANCELED);
        }

        if (saleRequest.saleItems() == null || saleRequest.saleItems().isEmpty()) {
            throw new BadRequestException(DomainConstants.SALE_ITEMS_REQUIRED);
        }

        List<Long> newProductIds = saleRequest.saleItems().stream()
                .map(SaleItemRequest::productId)
                .toList();
        Set<Long> uniqueNewProductIds = new HashSet<>(newProductIds);
        if (uniqueNewProductIds.size() != newProductIds.size()) {
            throw new BadRequestException(DomainConstants.DUPLICATE_PRODUCT);
        }

        List<SaleItem> oldItems = saleItemsPersistencePort.findAllBySaleId(saleId);

        Set<Long> allProductIds = new HashSet<>(uniqueNewProductIds);
        oldItems.forEach(item -> allProductIds.add(item.getProductId()));

        List<Product> products = productPersistencePort.findAllByIdsAndBusinessId(new ArrayList<>(allProductIds), businessId);
        Map<Long, Product> productsById = products.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        if (!productsById.keySet().containsAll(uniqueNewProductIds)) {
            throw new NotFoundException(DomainConstants.PRODUCT_NOT_FOUND);
        }

        Map<Long, Integer> oldQtyByProduct = oldItems.stream()
                .collect(Collectors.toMap(SaleItem::getProductId, SaleItem::getQuantity));
        Map<Long, Integer> newQtyByProduct = saleRequest.saleItems().stream()
                .collect(Collectors.toMap(SaleItemRequest::productId, SaleItemRequest::quantity));

        for (Long productId : allProductIds) {
            int delta = newQtyByProduct.getOrDefault(productId, 0) - oldQtyByProduct.getOrDefault(productId, 0);
            if (delta <= 0) continue;
            Product product = productsById.get(productId);
            if (product.getStock() < delta) {
                throw new ConflictException(DomainConstants.INSUFFICIENT_STOCK);
            }
        }

        for (Long productId : allProductIds) {
            int delta = newQtyByProduct.getOrDefault(productId, 0) - oldQtyByProduct.getOrDefault(productId, 0);
            if (delta == 0) continue;
            Product product = productsById.get(productId);
            product.setStock(product.getStock() - delta);
            productPersistencePort.saveProduct(product);
        }

        BigDecimal newTotal = BigDecimal.ZERO;
        for (SaleItemRequest item : saleRequest.saleItems()) {
            Product product = productsById.get(item.productId());
            BigDecimal unitPrice = item.unitPrice() != null ? item.unitPrice() : product.getSalePrice();
            newTotal = newTotal.add(unitPrice.multiply(BigDecimal.valueOf(item.quantity())));
        }

        if (saleRequest.paidAmount() == null
                || saleRequest.paidAmount().compareTo(BigDecimal.ZERO) < 0
                || saleRequest.paidAmount().compareTo(newTotal) > 0) {
            throw new BadRequestException(DomainConstants.INVALID_PAID_AMOUNT);
        }

        SaleStatus newStatus = saleRequest.paidAmount().compareTo(newTotal) >= 0
                ? SaleStatus.PAID
                : SaleStatus.PARTIAL;

        if (newStatus == SaleStatus.PARTIAL && saleRequest.clientId() == null) {
            throw new BadRequestException(DomainConstants.CLIENT_REQUIRED_FOR_PARTIAL_PAYMENT);
        }

        BigDecimal oldSalePaidAmount = sale.getPaidAmount();
        Debt existingDebt = debtPersistencePort.findBySaleId(saleId).orElse(null);
        BigDecimal abonosSoFar = existingDebt != null
                ? existingDebt.getPaidAmount().subtract(oldSalePaidAmount)
                : BigDecimal.ZERO;
        if (abonosSoFar.compareTo(BigDecimal.ZERO) < 0) {
            abonosSoFar = BigDecimal.ZERO;
        }

        BigDecimal newDebtPaidAmount = saleRequest.paidAmount().add(abonosSoFar);
        if (newDebtPaidAmount.compareTo(newTotal) > 0) {
            throw new BadRequestException(DomainConstants.SALE_TOTAL_BELOW_PAID);
        }

        if (existingDebt == null && newStatus == SaleStatus.PARTIAL) {
            Debt debt = new Debt();
            debt.setBusinessId(businessId);
            debt.setClientId(saleRequest.clientId());
            debt.setSaleId(saleId);
            debt.setTotalAmount(newTotal);
            debt.setPaidAmount(saleRequest.paidAmount());
            debt.setStatus(DebtStatus.PARTIAL);
            debt.setDueDate(LocalDateTime.now().plusDays(30).toLocalDate());
            debt.setCreatedAt(LocalDateTime.now());
            debtPersistencePort.save(debt);
        } else if (existingDebt != null) {
            existingDebt.setClientId(saleRequest.clientId());
            existingDebt.setTotalAmount(newTotal);
            existingDebt.setPaidAmount(newDebtPaidAmount);
            if (newDebtPaidAmount.compareTo(newTotal) >= 0) {
                existingDebt.setStatus(
                        abonosSoFar.compareTo(BigDecimal.ZERO) > 0
                                ? DebtStatus.PAID
                                : DebtStatus.CANCELLED
                );
            } else if (newDebtPaidAmount.compareTo(BigDecimal.ZERO) > 0) {
                existingDebt.setStatus(DebtStatus.PARTIAL);
            } else {
                existingDebt.setStatus(DebtStatus.PENDING);
            }
            debtPersistencePort.save(existingDebt);
        }

        sale.setClientId(saleRequest.clientId());
        sale.setTotal(newTotal);
        sale.setPaidAmount(saleRequest.paidAmount());
        sale.setStatus(newStatus);
        sale.setPaymentMethod(saleRequest.paymentMethod());
        Sale savedSale = salePersistencePort.saveSale(sale);

        saleItemsPersistencePort.deleteAllBySaleId(saleId);
        List<SaleItem> newItems = saleRequest.saleItems().stream()
                .map(item -> {
                    Product product = productsById.get(item.productId());
                    BigDecimal unitPrice = item.unitPrice() != null ? item.unitPrice() : product.getSalePrice();

                    SaleItem saleItem = new SaleItem();
                    saleItem.setSaleId(savedSale.getId());
                    saleItem.setProductId(product.getId());
                    saleItem.setQuantity(item.quantity());
                    saleItem.setUnitPrice(unitPrice);
                    saleItem.setSubtotal(unitPrice.multiply(BigDecimal.valueOf(item.quantity())));
                    return saleItem;
                })
                .toList();
        saleItemsPersistencePort.saveAll(newItems);

        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessId(businessId);
        auditLog.setUserId(userId);
        auditLog.setAction(DomainConstants.SALE_UPDATED);
        auditLog.setEntity(DomainConstants.SALE_ENTITY);
        auditLog.setEntityId(savedSale.getId());
        auditLog.setDetails(DomainConstants.SALE_UPDATED_DETAILS + savedSale.getId());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogsPersistencePort.save(auditLog);

        return new SaleResponse(savedSale, newItems);
    }

    @Override
    public SaleResponse updateSaleDate(Long businessId, Long saleId, LocalDateTime createdAt) {

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

        sale.setCreatedAt(createdAt);
        Sale savedSale = salePersistencePort.saveSale(sale);

        List<SaleItem> saleItems = saleItemsPersistencePort.findAllBySaleId(saleId);

        return new SaleResponse(savedSale, saleItems);
    }

    @Override
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

        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessId(businessId);
        auditLog.setUserId(userId);
        auditLog.setAction(DomainConstants.SALE_CANCELLED);
        auditLog.setEntity(DomainConstants.SALE_ENTITY);
        auditLog.setEntityId(savedSale.getId());
        auditLog.setDetails(DomainConstants.SALE_CANCELLED_DETAILS + savedSale.getId());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogsPersistencePort.save(auditLog);

        return new SaleResponse(
                savedSale,
                saleItems
        );
    }

    @Override
    public List<Sale> findSales(
            Long businessId,
            SaleStatus status,
            Long clientId,
            LocalDateTime from,
            LocalDateTime to
    ) {
        Long userId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() ->
                        new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND)
                );

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        if (from != null && to != null && from.isAfter(to)) {
            throw new BadRequestException(DomainConstants.INVALID_DATE_RANGE);
        }

        return salePersistencePort.findAllByFilters(
                businessId,
                status,
                clientId,
                from,
                to
        );
    }

    @Override
    public SaleResponse findSaleById(Long businessId, Long saleId) {

        Long userId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() ->
                        new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND)
                );

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Sale sale = salePersistencePort
                .findByIdAndBusinessId(saleId, businessId)
                .orElseThrow(() ->
                        new NotFoundException(DomainConstants.SALE_NOT_FOUND)
                );

        List<SaleItem> saleItems =
                saleItemsPersistencePort.findAllBySaleId(saleId);

        return new SaleResponse(
                sale,
                saleItems
        );
    }
}