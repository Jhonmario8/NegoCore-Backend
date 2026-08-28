package com.negocore.application.handler;

import com.negocore.application.dto.request.CategoryRequestDTO;
import com.negocore.application.dto.response.CategoryResponseDTO;

public interface ICategoryHandler {

    CategoryResponseDTO createCategory(Long businessId, CategoryRequestDTO categoryRequestDTO);

}
