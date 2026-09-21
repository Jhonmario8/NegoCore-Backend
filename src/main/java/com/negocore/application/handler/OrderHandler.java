package com.negocore.application.handler;

import com.negocore.application.dto.request.OrderConversionRequestDTO;
import com.negocore.application.dto.request.OrderItemRequestDTO;
import com.negocore.application.dto.request.OrderItemSaleRequestDTO;
import com.negocore.application.dto.response.OrderListResponseDTO;
import com.negocore.application.dto.response.OrderResponseDTO;
import com.negocore.application.dto.response.PurchaseResponseDTO;
import com.negocore.application.dto.response.SaleResponseDTO;
import com.negocore.application.mapper.IOrderMapper;
import com.negocore.application.mapper.IPurchaseMapper;
import com.negocore.application.mapper.ISaleMapper;
import com.negocore.domain.api.IOrderServicePort;
import com.negocore.domain.model.Order;
import com.negocore.domain.model.OrderResponse;
import com.negocore.domain.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderHandler implements IOrderHandler {

    private final IOrderServicePort orderServicePort;
    private final IOrderMapper orderMapper;
    private final IPurchaseMapper purchaseMapper;
    private final ISaleMapper saleMapper;

    @Override
    public OrderResponseDTO createOrder(Long businessId) {
        Order order = orderServicePort.createOrder(businessId);
        return orderMapper.toResponseDto(new OrderResponse(order, List.of()));
    }

    @Override
    public List<OrderListResponseDTO> findOrders(Long businessId, OrderStatus status) {
        return orderServicePort.findOrders(businessId, status)
                .stream()
                .map(orderMapper::toListResponseDto)
                .toList();
    }

    @Override
    public OrderResponseDTO findOrderById(Long businessId, Long orderId) {
        return orderMapper.toResponseDto(
                orderServicePort.findOrderById(businessId, orderId)
        );
    }

    @Override
    public OrderResponseDTO addItem(Long businessId, Long orderId, OrderItemRequestDTO orderItemRequestDTO) {
        return orderMapper.toResponseDto(
                orderServicePort.addItem(
                        businessId,
                        orderId,
                        orderMapper.toDomain(orderItemRequestDTO)
                )
        );
    }

    @Override
    public OrderResponseDTO removeItem(Long businessId, Long orderId, Long itemId) {
        return orderMapper.toResponseDto(
                orderServicePort.removeItem(businessId, orderId, itemId)
        );
    }

    @Override
    public OrderResponseDTO cancelOrder(Long businessId, Long orderId) {
        orderServicePort.cancelOrder(businessId, orderId);
        return orderMapper.toResponseDto(
                orderServicePort.findOrderById(businessId, orderId)
        );
    }

    @Override
    public PurchaseResponseDTO convertToPurchase(Long businessId, Long orderId, OrderConversionRequestDTO orderConversionRequestDTO) {
        return purchaseMapper.toResponseDto(
                orderServicePort.convertToPurchase(
                        businessId,
                        orderId,
                        orderMapper.toDomain(orderConversionRequestDTO)
                )
        );
    }

    @Override
    public SaleResponseDTO convertItemToSale(Long businessId, Long orderId, Long itemId, OrderItemSaleRequestDTO orderItemSaleRequestDTO) {
        return saleMapper.toResponseDto(
                orderServicePort.convertItemToSale(
                        businessId,
                        orderId,
                        itemId,
                        orderMapper.toDomain(orderItemSaleRequestDTO)
                )
        );
    }
}
