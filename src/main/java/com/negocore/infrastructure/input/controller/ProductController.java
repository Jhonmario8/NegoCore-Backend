package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.ProductRequestDTO;
import com.negocore.application.dto.request.ProductUpdateDTO;
import com.negocore.application.dto.request.StockPatchDTO;
import com.negocore.application.dto.response.ProductResponseDTO;
import com.negocore.application.handler.IProductHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/businesses")
@RequiredArgsConstructor
public class ProductController {

    private final IProductHandler productHandler;

    @PostMapping("/{businessId}/products")
    public ResponseEntity<ProductResponseDTO> createProduct(@PathVariable Long businessId, @Valid @RequestBody ProductRequestDTO productRequestDTO) {
        ProductResponseDTO productResponseDTO = productHandler.createProduct(businessId, productRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(productResponseDTO);
    }

    @PatchMapping("/{businessId}/products/{productId}/stock")
    public ResponseEntity<ProductResponseDTO> updateStock(@PathVariable Long businessId, @PathVariable Long productId, @Valid @RequestBody StockPatchDTO stockPatchDTO) {
        ProductResponseDTO productResponseDTO = productHandler.updateStock(businessId, productId, stockPatchDTO);
        return ResponseEntity.ok(productResponseDTO);
    }

    @PatchMapping("/{businessId}/products/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(@PathVariable Long businessId, @PathVariable Long productId, @Valid @RequestBody ProductUpdateDTO productUpdateDTO) {
        ProductResponseDTO productResponseDTO = productHandler.updateProduct(businessId, productId, productUpdateDTO);
        return ResponseEntity.ok(productResponseDTO);
    }

    @DeleteMapping("/{businessId}/products/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long businessId, @PathVariable Long productId) {
        productHandler.deleteProduct(businessId, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{businessId}/products")
    public ResponseEntity<List<ProductResponseDTO>> findProducts(
            @PathVariable Long businessId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean lowStock
    ) {
        return ResponseEntity.ok(
                productHandler.findProducts(
                        businessId,
                        categoryId,
                        lowStock
                )
        );
    }

    @GetMapping("/{businessId}/products/{productId}")
    public ResponseEntity<ProductResponseDTO> findProductById(
            @PathVariable Long businessId,
            @PathVariable Long productId
    ) {
        return ResponseEntity.ok(
                productHandler.findProductById(
                        businessId,
                        productId
                )
        );
    }

    @PostMapping(value = "/{businessId}/products/{productId}/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductResponseDTO> uploadProductImage(
            @PathVariable Long businessId,
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(
                productHandler.uploadProductImage(businessId, productId, file)
        );
    }
}
