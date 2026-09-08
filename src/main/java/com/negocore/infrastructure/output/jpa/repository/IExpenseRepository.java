package com.negocore.infrastructure.output.jpa.repository;


import com.negocore.infrastructure.output.jpa.entity.ExpenseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface IExpenseRepository extends JpaRepository<ExpenseEntity, Long> {

    @Query("""
    SELECT COALESCE(SUM(e.amount), 0)
    FROM ExpenseEntity e
    WHERE e.businessId = :businessId
      AND e.createdAt >= :from
      AND e.createdAt < :to
""")
    BigDecimal sumAmountByBusinessIdAndCreatedAtRange(
            @Param("businessId") Long businessId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

}
