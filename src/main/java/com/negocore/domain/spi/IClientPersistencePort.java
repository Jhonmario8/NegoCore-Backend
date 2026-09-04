package com.negocore.domain.spi;

import com.negocore.domain.model.Client;

public interface IClientPersistencePort {

    Client save(Client client);

}
