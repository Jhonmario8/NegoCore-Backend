package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.infrastructure.output.jpa.entity.OrderItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IOrderItemRepository extends JpaRepository<OrderItemEntity, Long> {

    List<OrderItemEntity> findAllByOrderId(Long orderId);

    Optional<OrderItemEntity> findByIdAndOrderId(Long itemId, Long orderId);

    void deleteByIdAndOrderId(Long itemId, Long orderId);
}
