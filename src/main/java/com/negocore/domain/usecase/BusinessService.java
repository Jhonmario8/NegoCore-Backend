package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IBusinessServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.Business;
import com.negocore.domain.spi.IBusinessPersistencePort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class BusinessService implements IBusinessServicePort {

    private final IBusinessPersistencePort businessPersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;

    @Override
    public Business createBusiness(Business business) {

        if (business.getCurrency() == null || business.getCurrency().isBlank()) {
            business.setCurrency(DomainConstants.DEFAULT_CURRENCY);
        }

        Long currentUserId = authenticationServicePort.getCurrentUserId();
        business.setOwnerId(currentUserId);
        business.setActive(true);
        business.setCreatedAt(LocalDateTime.now());

        return businessPersistencePort.saveBusiness(business);
    }

    @Override
    public List<Business> findAllBusinesses() {
        Long ownerId = authenticationServicePort.getCurrentUserId();

        return businessPersistencePort.findAllByOwnerId(ownerId);
    }

    @Override
    public Business updateBusiness(Long businessId, Business businessChanges) {
        Long currentUserId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(currentUserId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        if (businessChanges.getName() != null) {
            business.setName(businessChanges.getName());
        }
        if (businessChanges.getCurrency() != null) {
            business.setCurrency(businessChanges.getCurrency());
        }
        if (businessChanges.getAddress() != null) {
            business.setAddress(businessChanges.getAddress());
        }
        if (businessChanges.getPhone() != null) {
            business.setPhone(businessChanges.getPhone());
        }
        if (businessChanges.getEmail() != null) {
            business.setEmail(businessChanges.getEmail());
        }

        return businessPersistencePort.saveBusiness(business);
    }
}
