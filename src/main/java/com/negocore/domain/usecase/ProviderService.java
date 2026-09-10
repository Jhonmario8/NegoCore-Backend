package com.negocore.domain.usecase;


import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IProviderServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.Business;
import com.negocore.domain.model.Provider;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.domain.spi.IProviderPersistencePort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class ProviderService implements IProviderServicePort {

    private final IProviderPersistencePort providerPersistencePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;

    @Override
    public Provider createProvider(Long businessId, Provider provider) {

        Long userId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() ->
                        new NotFoundException(
                                DomainConstants.BUSINESS_NOT_FOUND
                        )
                );

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(
                    DomainConstants.BUSINESS_NOT_FOUND
            );
        }

        provider.setBusinessId(businessId);
        provider.setCreatedAt(LocalDateTime.now());

        return providerPersistencePort.save(provider);
    }

    @Override
    public List<Provider> findAllByBusinessId(Long businessId) {

        Long userId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() ->
                        new NotFoundException(
                                DomainConstants.BUSINESS_NOT_FOUND
                        )
                );

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(
                    DomainConstants.BUSINESS_NOT_FOUND
            );
        }

        return providerPersistencePort.findAllByBusinessId(businessId);
    }

}
