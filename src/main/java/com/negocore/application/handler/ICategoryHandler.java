package com.negocore.application.handler;

import com.negocore.application.dto.request.CategoryRequestDTO;
import com.negocore.application.dto.response.CategoryResponseDTO;

import java.util.List;

public interface ICategoryHandler {

    CategoryResponseDTO createCategory(Long businessId, CategoryRequestDTO categoryRequestDTO);
    List<CategoryResponseDTO> getCategoriesByBusinessId(Long businessId);
}
