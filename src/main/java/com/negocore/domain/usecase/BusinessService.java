package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IBusinessServicePort;
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
            business.setCurrency("COP");
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
}
