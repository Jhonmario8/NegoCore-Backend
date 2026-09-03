package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.infrastructure.output.jpa.entity.SaleItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ISaleItemRepository extends JpaRepository<SaleItemEntity, Long> {
    List<SaleItemEntity> findAllBySaleId(Long saleId);
}
