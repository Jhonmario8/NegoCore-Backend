package com.negocore.domain.api;

import com.negocore.domain.model.Provider;

import java.util.List;

public interface IProviderServicePort {

    Provider createProvider(Long businessId,Provider provider);

    List<Provider> findAllByBusinessId(Long businessId);
}
