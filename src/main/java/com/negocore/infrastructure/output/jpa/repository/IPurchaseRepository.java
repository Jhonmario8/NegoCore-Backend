package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.infrastructure.output.jpa.entity.PurchaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPurchaseRepository
        extends JpaRepository<PurchaseEntity, Long>,
        JpaSpecificationExecutor<PurchaseEntity> {

    Optional<PurchaseEntity> findByIdAndBusinessId(Long purchaseId, Long businessId);
}
