package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.ICategoryServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.ConflictException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.Business;
import com.negocore.domain.model.Category;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.domain.spi.ICategoryPersistencePort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

@RequiredArgsConstructor
public class CategoryService implements ICategoryServicePort {

    private final ICategoryPersistencePort categoryPersistencePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;

    @Override
    public Category createCategory(String name, Long businessId) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!Objects.equals(business.getOwnerId(), userId)){
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }
        if (categoryPersistencePort.existsByNameAndBusinessId(name, businessId)) {
            throw new ConflictException(DomainConstants.CATEGORY_ALREADY_EXISTS);
        }
        Category category = new Category();
        category.setName(name);
        category.setBusinessId(businessId);
        category.setActive(true);
        category.setCreatedAt(LocalDateTime.now());

        return categoryPersistencePort.saveCategory(category);
    }
}
