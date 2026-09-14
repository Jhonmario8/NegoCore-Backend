package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.infrastructure.output.jpa.entity.PurchaseItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IPurchaseItemRepository extends JpaRepository<PurchaseItemEntity, Long> {
    List<PurchaseItemEntity> findAllByPurchaseId(Long purchaseId);
}
