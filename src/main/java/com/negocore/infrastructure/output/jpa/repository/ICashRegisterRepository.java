package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.domain.model.CashRegisterStatus;
import com.negocore.infrastructure.output.jpa.entity.CashRegisterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ICashRegisterRepository extends JpaRepository<CashRegisterEntity, Long> {

    Boolean existsByBusinessIdAndStatus(Long businessId, CashRegisterStatus status);
    Optional<CashRegisterEntity> findByBusinessIdAndStatus(Long businessId, CashRegisterStatus status);

}
