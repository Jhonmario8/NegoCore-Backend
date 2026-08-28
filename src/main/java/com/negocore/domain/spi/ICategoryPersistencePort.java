package com.negocore.domain.spi;

import com.negocore.domain.model.Category;

public interface ICategoryPersistencePort {

    Category saveCategory(Category category);
    Boolean existsByNameAndBusinessId(String name, Long businessId);
    Boolean existsByIdAndBusinessId(Long categoryId, Long businessId);
}
