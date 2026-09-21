package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IOrderServicePort;
import com.negocore.domain.api.IPurchaseServicePort;
import com.negocore.domain.api.ISaleServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.AuditLog;
import com.negocore.domain.model.Business;
import com.negocore.domain.model.Order;
import com.negocore.domain.model.OrderConversionItemRequest;
import com.negocore.domain.model.OrderConversionRequest;
import com.negocore.domain.model.OrderItem;
import com.negocore.domain.model.OrderItemRequest;
import com.negocore.domain.model.OrderItemSaleRequest;
import com.negocore.domain.model.OrderResponse;
import com.negocore.domain.model.OrderStatus;
import com.negocore.domain.model.Product;
import com.negocore.domain.model.PurchaseItemRequest;
import com.negocore.domain.model.PurchaseRequest;
import com.negocore.domain.model.PurchaseResponse;
import com.negocore.domain.model.SaleItemRequest;
import com.negocore.domain.model.SaleRequest;
import com.negocore.domain.model.SaleResponse;
import com.negocore.domain.spi.IAuditLogsPersistencePort;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.domain.spi.IClientPersistencePort;
import com.negocore.domain.spi.IOrderItemsPersistencePort;
import com.negocore.domain.spi.IOrderPersistencePort;
import com.negocore.domain.spi.IProductPersistencePort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class OrderService implements IOrderServicePort {

    private final IOrderPersistencePort orderPersistencePort;
    private final IOrderItemsPersistencePort orderItemsPersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final IProductPersistencePort productPersistencePort;
    private final IClientPersistencePort clientPersistencePort;
    private final IPurchaseServicePort purchaseServicePort;
    private final ISaleServicePort saleServicePort;
    private final IAuditLogsPersistencePort auditLogsPersistencePort;

    @Override
    @Transactional
    public Order createOrder(Long businessId) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Integer nextNumber = orderPersistencePort.findMaxOrderNumberByBusinessId(businessId) + 1;

        Order order = new Order();
        order.setBusinessId(businessId);
        order.setOrderNumber(nextNumber);
        order.setStatus(OrderStatus.OPEN);
        order.setCreatedAt(LocalDateTime.now());

        Order savedOrder = orderPersistencePort.save(order);

        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessId(businessId);
        auditLog.setUserId(userId);
        auditLog.setAction(DomainConstants.ORDER_CREATED);
        auditLog.setEntity(DomainConstants.ORDER_ENTITY);
        auditLog.setEntityId(savedOrder.getId());
        auditLog.setDetails(DomainConstants.ORDER_CREATED_DETAILS + savedOrder.getId());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogsPersistencePort.save(auditLog);

        return savedOrder;
    }

    @Override
    public List<Order> findOrders(Long businessId, OrderStatus status) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        return orderPersistencePort.findAllByBusinessIdAndStatus(businessId, status);
    }

    @Override
    public OrderResponse findOrderById(Long businessId, Long orderId) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Order order = orderPersistencePort.findByIdAndBusinessId(orderId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.ORDER_NOT_FOUND));

        List<OrderItem> items = orderItemsPersistencePort.findAllByOrderId(orderId);

        return new OrderResponse(order, items);
    }

    @Override
    @Transactional
    public OrderResponse addItem(Long businessId, Long orderId, OrderItemRequest itemRequest) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Order order = orderPersistencePort.findByIdAndBusinessId(orderId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new BadRequestException(DomainConstants.ORDER_NOT_OPEN);
        }

        Product product = productPersistencePort.findByIdAndBusinessId(itemRequest.productId(), businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.PRODUCT_NOT_FOUND));

        if (itemRequest.clientId() != null) {
            clientPersistencePort.findByIdAndBusinessId(itemRequest.clientId(), businessId)
                    .orElseThrow(() -> new NotFoundException(DomainConstants.CLIENT_NOT_FOUND));
        }

        OrderItem item = new OrderItem();
        item.setOrderId(orderId);
        item.setProductId(product.getId());
        item.setQuantity(itemRequest.quantity());
        item.setClientId(itemRequest.clientId());
        item.setRequesterName(itemRequest.requesterName());
        item.setUnitCost(itemRequest.unitCost());
        item.setSalePrice(itemRequest.salePrice());
        item.setCreatedAt(LocalDateTime.now());

        orderItemsPersistencePort.save(item);

        return new OrderResponse(order, orderItemsPersistencePort.findAllByOrderId(orderId));
    }

    @Override
    @Transactional
    public OrderResponse removeItem(Long businessId, Long orderId, Long itemId) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Order order = orderPersistencePort.findByIdAndBusinessId(orderId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new BadRequestException(DomainConstants.ORDER_NOT_OPEN);
        }

        orderItemsPersistencePort.findByIdAndOrderId(itemId, orderId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.ORDER_ITEM_NOT_FOUND));

        orderItemsPersistencePort.deleteByIdAndOrderId(itemId, orderId);

        return new OrderResponse(order, orderItemsPersistencePort.findAllByOrderId(orderId));
    }

    @Override
    @Transactional
    public Order cancelOrder(Long businessId, Long orderId) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Order order = orderPersistencePort.findByIdAndBusinessId(orderId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new BadRequestException(DomainConstants.ORDER_NOT_OPEN);
        }

        order.setStatus(OrderStatus.CANCELLED);
        return orderPersistencePort.save(order);
    }

    @Override
    @Transactional
    public PurchaseResponse convertToPurchase(Long businessId, Long orderId, OrderConversionRequest conversionRequest) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Order order = orderPersistencePort.findByIdAndBusinessId(orderId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.OPEN) {
            throw new BadRequestException(DomainConstants.ORDER_NOT_OPEN);
        }

        List<OrderItem> orderItems = orderItemsPersistencePort.findAllByOrderId(orderId);

        if (orderItems.isEmpty()) {
            throw new BadRequestException(DomainConstants.PURCHASE_ITEMS_REQUIRED);
        }

        Map<Long, Integer> quantityByProduct = orderItems.stream()
                .collect(Collectors.groupingBy(OrderItem::getProductId, Collectors.summingInt(OrderItem::getQuantity)));

        Map<Long, BigDecimal> unitCostByProduct = conversionRequest.unitCosts().stream()
                .collect(Collectors.toMap(OrderConversionItemRequest::productId, OrderConversionItemRequest::unitCost));

        for (Long productId : quantityByProduct.keySet()) {
            if (!unitCostByProduct.containsKey(productId)) {
                throw new BadRequestException(DomainConstants.ORDER_MISSING_UNIT_COST);
            }
        }

        List<PurchaseItemRequest> purchaseItemRequests = quantityByProduct.entrySet().stream()
                .map(entry -> new PurchaseItemRequest(entry.getKey(), entry.getValue(), unitCostByProduct.get(entry.getKey())))
                .toList();

        PurchaseRequest purchaseRequest = new PurchaseRequest(
                conversionRequest.providerId(),
                purchaseItemRequests,
                conversionRequest.paymentMethod(),
                conversionRequest.paidAmount(),
                conversionRequest.shippingCost(),
                null
        );

        PurchaseResponse purchaseResponse = purchaseServicePort.registerPurchase(businessId, purchaseRequest);

        order.setStatus(OrderStatus.CONVERTED);
        order.setConvertedPurchaseId(purchaseResponse.getPurchase().getId());
        order.setConvertedAt(LocalDateTime.now());
        orderPersistencePort.save(order);

        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessId(businessId);
        auditLog.setUserId(userId);
        auditLog.setAction(DomainConstants.ORDER_CONVERTED);
        auditLog.setEntity(DomainConstants.ORDER_ENTITY);
        auditLog.setEntityId(order.getId());
        auditLog.setDetails(DomainConstants.ORDER_CONVERTED_DETAILS + purchaseResponse.getPurchase().getId());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogsPersistencePort.save(auditLog);

        return purchaseResponse;
    }

    @Override
    @Transactional
    public SaleResponse convertItemToSale(Long businessId, Long orderId, Long itemId, OrderItemSaleRequest saleRequest) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        orderPersistencePort.findByIdAndBusinessId(orderId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.ORDER_NOT_FOUND));

        OrderItem item = orderItemsPersistencePort.findByIdAndOrderId(itemId, orderId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.ORDER_ITEM_NOT_FOUND));

        if (item.getClientId() == null) {
            throw new BadRequestException(DomainConstants.ORDER_ITEM_NO_CLIENT);
        }

        if (item.getConvertedSaleId() != null) {
            throw new BadRequestException(DomainConstants.ORDER_ITEM_ALREADY_SOLD);
        }

        Product product = productPersistencePort.findByIdAndBusinessId(item.getProductId(), businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.PRODUCT_NOT_FOUND));

        BigDecimal unitPrice = item.getSalePrice() != null ? item.getSalePrice() : product.getSalePrice();

        SaleItemRequest saleItemRequest = new SaleItemRequest(item.getProductId(), item.getQuantity(), unitPrice);
        SaleRequest saleRequestDomain = new SaleRequest(
                List.of(saleItemRequest),
                saleRequest.paymentMethod(),
                saleRequest.paidAmount(),
                item.getClientId(),
                null
        );

        SaleResponse saleResponse = saleServicePort.registerSale(businessId, saleRequestDomain);

        item.setConvertedSaleId(saleResponse.getSale().getId());
        orderItemsPersistencePort.save(item);

        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessId(businessId);
        auditLog.setUserId(userId);
        auditLog.setAction(DomainConstants.ORDER_ITEM_SOLD);
        auditLog.setEntity(DomainConstants.ORDER_ENTITY);
        auditLog.setEntityId(orderId);
        auditLog.setDetails(DomainConstants.ORDER_ITEM_SOLD_DETAILS + saleResponse.getSale().getId());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogsPersistencePort.save(auditLog);

        return saleResponse;
    }
}
