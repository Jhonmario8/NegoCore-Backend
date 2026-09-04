package com.negocore.domain.api;

import com.negocore.domain.model.Client;

public interface IClientServicePort {

    Client registerClient(Long businessId, Client client);

}
