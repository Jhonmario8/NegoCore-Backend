package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.domain.model.PayableSource;
import com.negocore.infrastructure.output.jpa.entity.PayableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPayableRepository
        extends JpaRepository<PayableEntity, Long>,
        JpaSpecificationExecutor<PayableEntity> {

    Optional<PayableEntity> findBySourceAndSourceId(PayableSource source, Long sourceId);
}
