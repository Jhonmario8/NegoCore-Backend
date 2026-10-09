package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.domain.model.PurchaseStatus;
import com.negocore.infrastructure.output.jpa.entity.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface IPurchaseRepository
        extends JpaRepository<PurchaseEntity, Long>,
        JpaSpecificationExecutor<PurchaseEntity> {

    Optional<PurchaseEntity> findByIdAndBusinessId(Long purchaseId, Long businessId);

    @Query("""
        SELECT COALESCE(SUM(p.total), 0)
        FROM PurchaseEntity p
        WHERE p.businessId = :businessId
          AND p.createdAt >= :from
          AND p.createdAt < :to
          AND p.status <> :status
        """)
    BigDecimal sumTotalByBusinessIdAndCreatedAtRange(
            @Param("businessId") Long businessId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("status") PurchaseStatus status
    );
}
