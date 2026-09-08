package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.domain.model.SaleStatus;
import com.negocore.infrastructure.output.jpa.entity.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface ISaleRepository extends JpaRepository<SaleEntity, Long> {

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
