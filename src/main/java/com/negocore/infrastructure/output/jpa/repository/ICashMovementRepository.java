package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.domain.model.CashMovementType;
import com.negocore.infrastructure.output.jpa.entity.CashMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ICashMovementRepository extends JpaRepository<CashMovementEntity, Long> {

    List<CashMovementEntity> findByCashRegisterIdAndTypeIn(Long cashRegisterId, List<CashMovementType> types);

}
