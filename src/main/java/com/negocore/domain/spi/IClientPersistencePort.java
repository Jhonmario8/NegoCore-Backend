package com.negocore.domain.spi;

import com.negocore.domain.model.Client;

import java.util.List;
import java.util.Optional;

public interface IClientPersistencePort {

    Client save(Client client);
    List<Client> findAllByBusinessId(Long businessId);
    Optional<Client> findByIdAndBusinessId(Long clientId, Long businessId);
}
