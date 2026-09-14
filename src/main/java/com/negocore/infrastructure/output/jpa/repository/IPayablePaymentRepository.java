package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.infrastructure.output.jpa.entity.PayablePaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPayablePaymentRepository extends JpaRepository<PayablePaymentEntity, Long> {
}
