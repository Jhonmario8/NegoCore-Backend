package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.infrastructure.output.jpa.entity.DebtPaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IDebtPaymentRepository extends JpaRepository<DebtPaymentEntity, Long> {

    boolean existsByDebtId(Long debtId);
}
