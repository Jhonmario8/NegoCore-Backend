package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IPurchaseServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
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
public class PurchaseService implements IPurchaseServicePort {

    private final IPurchasePersistencePort purchasePersistencePort;
    private final IPurchaseItemsPersistencePort purchaseItemsPersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final IProviderPersistencePort providerPersistencePort;
    private final IProductPersistencePort productPersistencePort;
    private final IPayablePersistencePort payablePersistencePort;
    private final IAuditLogsPersistencePort auditLogsPersistencePort;

    @Override
    @Transactional
    public PurchaseResponse registerPurchase(Long businessId, PurchaseRequest purchaseRequest) {

        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Provider provider = providerPersistencePort.findByIdAndBusinessId(purchaseRequest.providerId(), businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.PROVIDER_NOT_FOUND));

        if (purchaseRequest.purchaseItems() == null || purchaseRequest.purchaseItems().isEmpty()) {
            throw new BadRequestException(DomainConstants.PURCHASE_ITEMS_REQUIRED);
        }

        List<Long> productIds = purchaseRequest.purchaseItems().stream()
                .map(PurchaseItemRequest::productId)
                .toList();

        Set<Long> uniqueProductIds = new HashSet<>(productIds);

        List<Product> products = productPersistencePort.findAllByIdsAndBusinessId(productIds, businessId);

        if (products.size() != uniqueProductIds.size()) {
            throw new NotFoundException(DomainConstants.PRODUCT_NOT_FOUND);
        }

        Map<Long, Product> productsById = products.stream()
                .collect(Collectors.toMap(
                        Product::getId,
                        Function.identity()
                ));

        BigDecimal itemsTotal = BigDecimal.ZERO;
        for (PurchaseItemRequest item : purchaseRequest.purchaseItems()) {
            BigDecimal subtotal = item.unitCost().multiply(BigDecimal.valueOf(item.quantity()));
            itemsTotal = itemsTotal.add(subtotal);
        }

        BigDecimal shippingCost = purchaseRequest.shippingCost() != null
                ? purchaseRequest.shippingCost()
                : BigDecimal.ZERO;

        BigDecimal total = itemsTotal.add(shippingCost);

        if (purchaseRequest.paidAmount() == null
                || purchaseRequest.paidAmount().compareTo(BigDecimal.ZERO) < 0
                || purchaseRequest.paidAmount().compareTo(total) > 0) {
            throw new BadRequestException(DomainConstants.INVALID_PAID_AMOUNT);
        }

        PurchaseStatus status =
                purchaseRequest.paidAmount().compareTo(total) >= 0
                        ? PurchaseStatus.PAID
                        : PurchaseStatus.PARTIAL;

        for (PurchaseItemRequest item : purchaseRequest.purchaseItems()) {
            Product product = productsById.get(item.productId());
            product.setStock(product.getStock() + item.quantity());
            productPersistencePort.saveProduct(product);
        }

        Purchase purchase = new Purchase();
        purchase.setBusinessId(businessId);
        purchase.setProviderId(provider.getId());
        purchase.setTotal(total);
        purchase.setShippingCost(shippingCost);
        purchase.setPaidAmount(purchaseRequest.paidAmount());
        purchase.setStatus(status);
        purchase.setPaymentMethod(purchaseRequest.paymentMethod());
        purchase.setCreatedAt(purchaseRequest.createdAt() != null ? purchaseRequest.createdAt() : LocalDateTime.now());

        Purchase savedPurchase = purchasePersistencePort.savePurchase(purchase);

        if (status == PurchaseStatus.PARTIAL) {
            Payable payable = new Payable();
            payable.setBusinessId(businessId);
            payable.setPayeeType(PayeeType.PROVIDER);
            payable.setProviderId(provider.getId());
            payable.setSource(PayableSource.PURCHASE);
            payable.setSourceId(savedPurchase.getId());
            payable.setTotalAmount(total.subtract(purchaseRequest.paidAmount()));
            payable.setPaidAmount(BigDecimal.ZERO);
            payable.setStatus(PayableStatus.PENDING);
            payable.setCreatedAt(LocalDateTime.now());
            payablePersistencePort.save(payable);
        }

        List<PurchaseItem> purchaseItems = purchaseRequest.purchaseItems()
                .stream()
                .map(item -> {
                    Product product = productsById.get(item.productId());

                    PurchaseItem purchaseItem = new PurchaseItem();
                    purchaseItem.setPurchaseId(savedPurchase.getId());
                    purchaseItem.setProductId(product.getId());
                    purchaseItem.setQuantity(item.quantity());
                    purchaseItem.setUnitCost(item.unitCost());
                    purchaseItem.setSubtotal(item.unitCost().multiply(BigDecimal.valueOf(item.quantity())));

                    return purchaseItem;
                })
                .toList();
        purchaseItemsPersistencePort.saveAll(purchaseItems);

        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessId(businessId);
        auditLog.setUserId(userId);
        auditLog.setAction(DomainConstants.PURCHASE_CREATED);
        auditLog.setEntity(DomainConstants.PURCHASE_ENTITY);
        auditLog.setEntityId(savedPurchase.getId());
        auditLog.setDetails(DomainConstants.PURCHASE_CREATED_DETAILS + savedPurchase.getId());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogsPersistencePort.save(auditLog);

        return new PurchaseResponse(savedPurchase, purchaseItems);
    }

    @Override
    @Transactional
    public PurchaseResponse updatePurchaseDate(Long businessId, Long purchaseId, LocalDateTime createdAt) {
        Long userId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Purchase purchase = purchasePersistencePort.findByIdAndBusinessId(purchaseId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.PURCHASE_NOT_FOUND));

        purchase.setCreatedAt(createdAt);
        Purchase savedPurchase = purchasePersistencePort.savePurchase(purchase);

        List<PurchaseItem> purchaseItems = purchaseItemsPersistencePort.findAllByPurchaseId(purchaseId);

        return new PurchaseResponse(savedPurchase, purchaseItems);
    }

    @Override
    public List<Purchase> findPurchases(
            Long businessId,
            Long providerId,
            PurchaseStatus status,
            LocalDateTime from,
            LocalDateTime to
    ) {
        Long userId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        if (from != null && to != null && from.isAfter(to)) {
            throw new BadRequestException(DomainConstants.INVALID_DATE_RANGE);
        }

        return purchasePersistencePort.findAllByFilters(
                businessId,
                providerId,
                status,
                from,
                to
        );
    }

    @Override
    public PurchaseResponse findPurchaseById(Long businessId, Long purchaseId) {
        Long userId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Purchase purchase = purchasePersistencePort.findByIdAndBusinessId(purchaseId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.PURCHASE_NOT_FOUND));

        List<PurchaseItem> purchaseItems = purchaseItemsPersistencePort.findAllByPurchaseId(purchaseId);

        return new PurchaseResponse(purchase, purchaseItems);
    }
}
