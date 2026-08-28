package com.negocore.infrastructure.output.jpa.repository;

import com.negocore.infrastructure.output.jpa.entity.CategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICategoryRepository extends JpaRepository<CategoryEntity, Long> {

    Boolean existsByBusinessIdAndName(Long businessId, String name);

}
