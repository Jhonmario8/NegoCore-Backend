package com.negocore.application.handler;

import com.negocore.application.dto.request.CategoryRequestDTO;
import com.negocore.application.dto.response.CategoryResponseDTO;
import com.negocore.application.mapper.ICategoryMapper;
import com.negocore.domain.api.ICategoryServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryHandler implements ICategoryHandler{

    private final ICategoryServicePort categoryServicePort;
    private final ICategoryMapper mapper;

    @Override
    public CategoryResponseDTO createCategory(Long businessId, CategoryRequestDTO categoryRequestDTO) {
        return mapper.toResponse(categoryServicePort.createCategory(categoryRequestDTO.getName(), businessId));
    }

    @Override
    public List<CategoryResponseDTO> getCategoriesByBusinessId(Long businessId) {

        return categoryServicePort.getCategoriesByBusinessId(businessId).stream()
                .map(mapper::toResponse)
                .toList();
    }
}
