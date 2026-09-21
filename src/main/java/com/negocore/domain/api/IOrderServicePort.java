package com.negocore.domain.api;

import com.negocore.domain.model.Order;
import com.negocore.domain.model.OrderConversionRequest;
import com.negocore.domain.model.OrderItemRequest;
import com.negocore.domain.model.OrderResponse;
import com.negocore.domain.model.OrderStatus;
import com.negocore.domain.model.PurchaseResponse;

import java.util.List;

public interface IOrderServicePort {

    Order createOrder(Long businessId);

    List<Order> findOrders(Long businessId, OrderStatus status);

    OrderResponse findOrderById(Long businessId, Long orderId);

    OrderResponse addItem(Long businessId, Long orderId, OrderItemRequest itemRequest);

    OrderResponse removeItem(Long businessId, Long orderId, Long itemId);

    Order cancelOrder(Long businessId, Long orderId);

    PurchaseResponse convertToPurchase(Long businessId, Long orderId, OrderConversionRequest conversionRequest);
}
