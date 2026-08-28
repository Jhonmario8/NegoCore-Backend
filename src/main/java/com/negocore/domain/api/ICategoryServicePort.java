package com.negocore.domain.api;

import com.negocore.domain.model.Category;

public interface ICategoryServicePort {

    Category createCategory(String name, Long businessId);

}
