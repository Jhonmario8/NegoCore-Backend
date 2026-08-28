package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.infrastructure.output.jpa.entity.BusinessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBusinessRepository extends JpaRepository<BusinessEntity, Long> {

    List<BusinessEntity> findByOwnerId(Long ownerId);


}
