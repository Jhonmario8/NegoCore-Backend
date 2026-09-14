package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.infrastructure.output.jpa.entity.ProviderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IProviderRepository extends JpaRepository<ProviderEntity, Long> {

    List<ProviderEntity> findAllByBusinessId(Long businessId);

    Optional<ProviderEntity> findByIdAndBusinessId(Long providerId, Long businessId);

}