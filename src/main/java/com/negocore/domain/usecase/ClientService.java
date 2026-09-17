package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IClientServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.Business;
import com.negocore.domain.model.Client;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.domain.spi.IClientPersistencePort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class ClientService implements IClientServicePort {

    private final IClientPersistencePort clientPersistencePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;
    @Override
    public Client registerClient(Long businessId, Client client) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }
        client.setBusinessId(businessId);
        client.setCreatedAt(LocalDateTime.now());
        return clientPersistencePort.save(client);
    }

    @Override
    public List<Client> getClientsByBusinessId(Long businessId) {
        return clientPersistencePort.findAllByBusinessId(businessId);
    }

    @Override
    public Client updateClient(Long businessId, Long clientId, Client clientChanges) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Client client = clientPersistencePort.findByIdAndBusinessId(clientId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.CLIENT_NOT_FOUND));

        if (clientChanges.getName() != null) {
            client.setName(clientChanges.getName());
        }
        if (clientChanges.getPhone() != null) {
            client.setPhone(clientChanges.getPhone());
        }
        if (clientChanges.getEmail() != null) {
            client.setEmail(clientChanges.getEmail());
        }
        if (clientChanges.getAddress() != null) {
            client.setAddress(clientChanges.getAddress());
        }

        return clientPersistencePort.save(client);
    }
}
