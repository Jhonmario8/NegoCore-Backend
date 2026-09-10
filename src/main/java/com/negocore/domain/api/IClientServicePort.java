package com.negocore.domain.api;

import com.negocore.domain.model.Client;

import java.util.List;

public interface IClientServicePort {

    Client registerClient(Long businessId, Client client);
    List<Client> getClientsByBusinessId(Long businessId);
}
