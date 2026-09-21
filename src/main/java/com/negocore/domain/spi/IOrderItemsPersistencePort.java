package com.negocore.domain.spi;

import com.negocore.domain.model.OrderItem;

import java.util.List;
import java.util.Optional;

public interface IOrderItemsPersistencePort {

    OrderItem save(OrderItem item);
    List<OrderItem> findAllByOrderId(Long orderId);
    Optional<OrderItem> findByIdAndOrderId(Long itemId, Long orderId);
    void deleteByIdAndOrderId(Long itemId, Long orderId);
}
