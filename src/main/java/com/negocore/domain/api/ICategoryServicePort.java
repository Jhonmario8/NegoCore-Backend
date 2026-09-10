package com.negocore.domain.api;

import com.negocore.domain.model.Category;

import java.util.List;

public interface ICategoryServicePort {

    Category createCategory(String name, Long businessId);
    List<Category> getCategoriesByBusinessId(Long businessId);
}
