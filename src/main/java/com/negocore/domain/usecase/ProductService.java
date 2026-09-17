package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IProductServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.ConflictException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.Business;
import com.negocore.domain.model.Product;
import com.negocore.domain.model.ProductImage;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.domain.spi.ICategoryPersistencePort;
import com.negocore.domain.spi.IProductPersistencePort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class ProductService implements IProductServicePort {

    private static final long MAX_IMAGE_SIZE_BYTES = 3L * 1024 * 1024;
    private static final Map<String, String> ALLOWED_IMAGE_TYPES = Map.of(
            "image/png", "png",
            "image/jpeg", "jpg",
            "image/webp", "webp"
    );

    private final IProductPersistencePort productPersistencePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;
    private final ICategoryPersistencePort categoryPersistencePort;

    @Override
    public Product createProduct(Long businessId, Product product) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }
        if (product.getSku() != null && productPersistencePort.existByBusinessIdAndSku(businessId, product.getSku())) {
            throw new ConflictException(DomainConstants.SKU_ALREADY_EXISTS);
        }
        if (product.getCategoryId() != null) {
            if (!categoryPersistencePort.existsByIdAndBusinessId(product.getCategoryId(), businessId)) {
                throw new NotFoundException(DomainConstants.Category_NOT_FOUND);
            }
        }
        product.setBusinessId(businessId);
        product.setActive(true);
        product.setCreatedAt(LocalDateTime.now());
        return productPersistencePort.saveProduct(product);
    }

    @Override
    public Product updateStock(Long businessId, Long productId, int quantity, String reason) {
        if (quantity == 0) {
            throw new BadRequestException(DomainConstants.QUANTITY_INVALID);
        }
        if (reason == null || reason.isBlank() || reason.length() > 200) {
            throw new BadRequestException(DomainConstants.REASON_INVALID);
        }

        Long userId = authenticationServicePort.getCurrentUserId();
        Product product = productPersistencePort.findById(productId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.PRODUCT_NOT_FOUND));
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }
        if (!product.getBusinessId().equals(businessId)) {
            throw new NotFoundException(DomainConstants.PRODUCT_NOT_FOUND);
        }
        int newStock = product.getStock() + quantity;
        if (newStock < 0) {
            throw new BadRequestException(DomainConstants.INSUFFICIENT_STOCK);
        }
        product.setStock(newStock);
        return productPersistencePort.saveProduct(product);

    }

    @Override
    public Product updateProduct(Long businessId, Long productId, Product productChanges) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Product product = productPersistencePort.findByIdAndBusinessId(productId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.PRODUCT_NOT_FOUND));

        if (productChanges.getName() != null) {
            product.setName(productChanges.getName());
        }
        if (productChanges.getSalePrice() != null) {
            product.setSalePrice(productChanges.getSalePrice());
        }

        return productPersistencePort.saveProduct(product);
    }

    @Override
    public List<Product> findProducts(
            Long businessId,
            Long categoryId,
            Boolean lowStock
    ) {
        Long userId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() ->
                        new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND)
                );

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        if (categoryId != null &&
                !categoryPersistencePort.existsByIdAndBusinessId(categoryId, businessId)) {
            throw new NotFoundException(DomainConstants.Category_NOT_FOUND);
        }

        return productPersistencePort.findAllByBusinessId(
                businessId,
                categoryId,
                lowStock
        );
    }
    @Override
    public Product findProductById(Long businessId, Long productId) {
        Long userId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() ->
                        new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND)
                );

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        return productPersistencePort.findByIdAndBusinessId(productId, businessId)
                .orElseThrow(() ->
                        new NotFoundException(DomainConstants.PRODUCT_NOT_FOUND)
                );
    }

    @Override
    public Product uploadProductImage(
            Long businessId,
            Long productId,
            String contentType,
            long size,
            byte[] content
    ) {
        Long userId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Product product = productPersistencePort.findByIdAndBusinessId(productId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.PRODUCT_NOT_FOUND));

        String extension = ALLOWED_IMAGE_TYPES.get(contentType);
        if (extension == null) {
            throw new BadRequestException(DomainConstants.INVALID_IMAGE_TYPE);
        }

        if (size > MAX_IMAGE_SIZE_BYTES) {
            throw new BadRequestException(DomainConstants.IMAGE_TOO_LARGE);
        }

        product.setImageData(content);
        product.setImageContentType(contentType);
        product.setImageUrl("/businesses/" + businessId + "/products/" + productId + "/image");
        return productPersistencePort.saveProduct(product);
    }

    @Override
    public ProductImage getProductImage(Long businessId, Long productId) {
        Product product = productPersistencePort.findByIdAndBusinessId(productId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.PRODUCT_NOT_FOUND));

        if (product.getImageData() == null) {
            throw new NotFoundException(DomainConstants.PRODUCT_NOT_FOUND);
        }

        return new ProductImage(product.getImageData(), product.getImageContentType());
    }

}
