package com.negocore.domain.spi;

import com.negocore.domain.model.Order;
import com.negocore.domain.model.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface IOrderPersistencePort {

    Order save(Order order);
    Optional<Order> findByIdAndBusinessId(Long orderId, Long businessId);
    List<Order> findAllByBusinessIdAndStatus(Long businessId, OrderStatus status);
    Integer findMaxOrderNumberByBusinessId(Long businessId);
}
