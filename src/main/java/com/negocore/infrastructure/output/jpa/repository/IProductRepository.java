package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.infrastructure.output.jpa.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProductRepository extends JpaRepository<ProductEntity, Long> {

    boolean existsByBusinessIdAndSku(Long businessId, String sku);

    List<ProductEntity> findAllByIdInAndBusinessId(List<Long> productIds, Long businessId);

    List<ProductEntity> findAllByIdIn(List<Long> productIds);

    Optional<ProductEntity> findByIdAndBusinessId(Long productId, Long businessId);

    @Query("""
        SELECT p
        FROM ProductEntity p
        WHERE p.businessId = :businessId
          AND (:categoryId IS NULL OR p.categoryId = :categoryId)
          AND (
              :lowStock IS NULL
              OR :lowStock = false
              OR p.stock <= p.minStockAlert
          )
        """)
    List<ProductEntity> findAllByBusinessId(
            @Param("businessId") Long businessId,
            @Param("categoryId") Long categoryId,
            @Param("lowStock") Boolean lowStock
    );
}
