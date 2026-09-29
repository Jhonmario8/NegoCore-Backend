package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.CategoryRequestDTO;
import com.negocore.application.dto.response.CategoryResponseDTO;
import com.negocore.application.handler.ICategoryHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/businesses")
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryHandler categoryHandler;

    @PostMapping("/{businessId}/categories")
    public ResponseEntity<CategoryResponseDTO> createCategory(@PathVariable Long businessId, @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        CategoryResponseDTO categoryResponseDTO = categoryHandler.createCategory(businessId, categoryRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponseDTO);
    }

    @GetMapping("/{businessId}/categories")
    public ResponseEntity<List<CategoryResponseDTO>> getCategoriesByBusinessId(@PathVariable Long businessId) {
        return ResponseEntity.ok(categoryHandler.getCategoriesByBusinessId(businessId));
    }
}
