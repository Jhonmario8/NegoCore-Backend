package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.BusinessCreateDTO;
import com.negocore.application.dto.request.CategoryRequestDTO;
import com.negocore.application.dto.request.ProductRequestDTO;
import com.negocore.application.dto.response.BusinessListResponseDTO;
import com.negocore.application.dto.response.BusinessResponseDTO;
import com.negocore.application.dto.response.CategoryResponseDTO;
import com.negocore.application.dto.response.ProductResponseDTO;
import com.negocore.application.handler.IBusinessHandler;
import com.negocore.application.handler.ICategoryHandler;
import com.negocore.application.handler.IProductHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final IBusinessHandler businessHandler;
    private final ICategoryHandler categoryHandler;
    private final IProductHandler productHandler;

    @PostMapping()
    public ResponseEntity<BusinessResponseDTO> createBusiness(@Valid @RequestBody BusinessCreateDTO businessCreateDTO) {
        BusinessResponseDTO businessResponseDTO = businessHandler.createBusiness(businessCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(businessResponseDTO);
    }

    @GetMapping
    public ResponseEntity<List<BusinessListResponseDTO>> findAllBusinesses() {
        List<BusinessListResponseDTO> businesses = businessHandler.findAllBusiness();
        return ResponseEntity.ok(businesses);
    }

    @PostMapping("/{businessId}/categories")
    public ResponseEntity<CategoryResponseDTO> createCategory(@PathVariable Long businessId, @Valid @RequestBody CategoryRequestDTO categoryRequestDTO) {
        CategoryResponseDTO categoryResponseDTO = categoryHandler.createCategory(businessId, categoryRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryResponseDTO);
    }

    @PostMapping("/{businessId}/products")
    public ResponseEntity<ProductResponseDTO> createProduct(@PathVariable Long businessId, @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO productResponseDTO = productHandler.createProduct(businessId, productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDTO);
    }
}
