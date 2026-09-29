package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.OrderConversionRequestDTO;
import com.negocore.application.dto.request.OrderItemRequestDTO;
import com.negocore.application.dto.request.OrderItemSaleRequestDTO;
import com.negocore.application.dto.response.OrderListResponseDTO;
import com.negocore.application.dto.response.OrderResponseDTO;
import com.negocore.application.dto.response.PurchaseResponseDTO;
import com.negocore.application.dto.response.SaleResponseDTO;
import com.negocore.application.handler.IOrderHandler;
import com.negocore.domain.model.OrderStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/businesses")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderHandler orderHandler;

    @PostMapping("/{businessId}/orders")
    public ResponseEntity<OrderResponseDTO> createOrder(@PathVariable Long businessId) {
        OrderResponseDTO orderResponseDTO = orderHandler.createOrder(businessId);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDTO);
    }

    @GetMapping("/{businessId}/orders")
    public ResponseEntity<List<OrderListResponseDTO>> findOrders(
            @PathVariable Long businessId,
            @RequestParam(required = false) OrderStatus status
    ) {
        return ResponseEntity.ok(orderHandler.findOrders(businessId, status));
    }

    @GetMapping("/{businessId}/orders/{orderId}")
    public ResponseEntity<OrderResponseDTO> findOrderById(
            @PathVariable Long businessId,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderHandler.findOrderById(businessId, orderId));
    }

    @PostMapping("/{businessId}/orders/{orderId}/items")
    public ResponseEntity<OrderResponseDTO> addOrderItem(
            @PathVariable Long businessId,
            @PathVariable Long orderId,
            @Valid @RequestBody OrderItemRequestDTO orderItemRequestDTO
    ) {
        OrderResponseDTO orderResponseDTO = orderHandler.addItem(businessId, orderId, orderItemRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderResponseDTO);
    }

    @DeleteMapping("/{businessId}/orders/{orderId}/items/{itemId}")
    public ResponseEntity<OrderResponseDTO> removeOrderItem(
            @PathVariable Long businessId,
            @PathVariable Long orderId,
            @PathVariable Long itemId
    ) {
        return ResponseEntity.ok(orderHandler.removeItem(businessId, orderId, itemId));
    }

    @PatchMapping("/{businessId}/orders/{orderId}/cancel")
    public ResponseEntity<OrderResponseDTO> cancelOrder(
            @PathVariable Long businessId,
            @PathVariable Long orderId
    ) {
        return ResponseEntity.ok(orderHandler.cancelOrder(businessId, orderId));
    }

    @PostMapping("/{businessId}/orders/{orderId}/convert")
    public ResponseEntity<PurchaseResponseDTO> convertOrderToPurchase(
            @PathVariable Long businessId,
            @PathVariable Long orderId,
            @Valid @RequestBody OrderConversionRequestDTO orderConversionRequestDTO
    ) {
        PurchaseResponseDTO purchaseResponseDTO =
                orderHandler.convertToPurchase(businessId, orderId, orderConversionRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(purchaseResponseDTO);
    }

    @PostMapping("/{businessId}/orders/{orderId}/items/{itemId}/convert-to-sale")
    public ResponseEntity<SaleResponseDTO> convertOrderItemToSale(
            @PathVariable Long businessId,
            @PathVariable Long orderId,
            @PathVariable Long itemId,
            @Valid @RequestBody OrderItemSaleRequestDTO orderItemSaleRequestDTO
    ) {
        SaleResponseDTO saleResponseDTO =
                orderHandler.convertItemToSale(businessId, orderId, itemId, orderItemSaleRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saleResponseDTO);
    }
}
