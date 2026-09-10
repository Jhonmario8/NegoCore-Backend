package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.domain.model.SaleStatus;
import com.negocore.infrastructure.output.jpa.entity.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ISaleRepository
        extends JpaRepository<SaleEntity, Long>,
        JpaSpecificationExecutor<SaleEntity> {

    Optional<SaleEntity> findByIdAndBusinessId(
            Long saleId,
            Long businessId
    );

    @Query("""
        SELECT s
        FROM SaleEntity s
        WHERE s.businessId = :businessId
          AND (:status IS NULL OR s.status = :status)
          AND (:clientId IS NULL OR s.clientId = :clientId)
          AND (:from IS NULL OR s.createdAt >= :from)
          AND (:to IS NULL OR s.createdAt < :to)
        ORDER BY s.createdAt DESC
        """)
    List<SaleEntity> findAllByFilters(
            @Param("businessId") Long businessId,
            @Param("status") SaleStatus status,
            @Param("clientId") Long clientId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
        SELECT COALESCE(SUM(s.total), 0)
        FROM SaleEntity s
        WHERE s.businessId = :businessId
          AND s.createdAt >= :from
          AND s.createdAt < :to
          AND s.status <> :status
        """)
    BigDecimal sumTotalByBusinessIdAndCreatedAtRange(
            @Param("businessId") Long businessId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("status") SaleStatus status
    );

    @Query("""
        SELECT COUNT(s)
        FROM SaleEntity s
        WHERE s.businessId = :businessId
          AND s.createdAt >= :from
          AND s.createdAt < :to
          AND s.status <> :status
        """)
    Long countByBusinessIdAndCreatedAtRange(
            @Param("businessId") Long businessId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("status") SaleStatus status
    );
}