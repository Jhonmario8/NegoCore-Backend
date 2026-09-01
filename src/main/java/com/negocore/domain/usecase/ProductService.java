package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IProductServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.ConflictException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.Business;
import com.negocore.domain.model.Product;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.domain.spi.ICategoryPersistencePort;
import com.negocore.domain.spi.IProductPersistencePort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class ProductService implements IProductServicePort {

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
}