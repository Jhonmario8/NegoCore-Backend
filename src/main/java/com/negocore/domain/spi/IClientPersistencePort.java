package com.negocore.domain.spi;

import com.negocore.domain.model.Client;

import java.util.List;

public interface IClientPersistencePort {

    Client save(Client client);
    List<Client> findAllByBusinessId(Long businessId);
}
