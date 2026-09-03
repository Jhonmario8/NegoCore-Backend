package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.infrastructure.output.jpa.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IProductRepository extends JpaRepository<ProductEntity, Long> {

    boolean existsByBusinessIdAndSku(Long businessId, String sku);
    List<ProductEntity> findAllByIdInAndBusinessId(List<Long> productIds, Long businessId);

    List<ProductEntity> findAllByIdIn(List<Long> productIds);
}
