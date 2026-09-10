package com.negocore.domain.spi;

import com.negocore.domain.model.Category;

import java.util.List;

public interface ICategoryPersistencePort {

    Category saveCategory(Category category);
    Boolean existsByNameAndBusinessId(String name, Long businessId);
    Boolean existsByIdAndBusinessId(Long categoryId, Long businessId);
    List<Category> findAllByBusinessId(Long businessId);
}
