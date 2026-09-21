package com.negocore.application.handler;

import com.negocore.application.dto.request.OrderConversionRequestDTO;
import com.negocore.application.dto.request.OrderItemRequestDTO;
import com.negocore.application.dto.response.OrderListResponseDTO;
import com.negocore.application.dto.response.OrderResponseDTO;
import com.negocore.application.dto.response.PurchaseResponseDTO;
import com.negocore.domain.model.OrderStatus;

import java.util.List;

public interface IOrderHandler {

    OrderResponseDTO createOrder(Long businessId);

    List<OrderListResponseDTO> findOrders(Long businessId, OrderStatus status);

    OrderResponseDTO findOrderById(Long businessId, Long orderId);

    OrderResponseDTO addItem(Long businessId, Long orderId, OrderItemRequestDTO orderItemRequestDTO);

    OrderResponseDTO removeItem(Long businessId, Long orderId, Long itemId);

    OrderResponseDTO cancelOrder(Long businessId, Long orderId);

    PurchaseResponseDTO convertToPurchase(Long businessId, Long orderId, OrderConversionRequestDTO orderConversionRequestDTO);
}
