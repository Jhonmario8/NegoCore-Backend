package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.domain.model.OrderStatus;
import com.negocore.infrastructure.output.jpa.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IOrderRepository extends JpaRepository<OrderEntity, Long> {

    Optional<OrderEntity> findByIdAndBusinessId(Long orderId, Long businessId);

    @Query("""
        SELECT o
        FROM OrderEntity o
        WHERE o.businessId = :businessId
          AND (:status IS NULL OR o.status = :status)
        ORDER BY o.orderNumber DESC
        """)
    List<OrderEntity> findAllByBusinessIdAndOptionalStatus(
            @Param("businessId") Long businessId,
            @Param("status") OrderStatus status
    );

    @Query("SELECT COALESCE(MAX(o.orderNumber), 0) FROM OrderEntity o WHERE o.businessId = :businessId")
    Integer findMaxOrderNumberByBusinessId(@Param("businessId") Long businessId);
}
