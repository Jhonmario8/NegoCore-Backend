package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.infrastructure.output.jpa.entity.DebtEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IDebtRepository extends JpaRepository<DebtEntity, Long> {

    Optional<DebtEntity> findBySaleId(Long saleId);
}
