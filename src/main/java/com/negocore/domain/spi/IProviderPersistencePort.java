package com.negocore.domain.spi;

import com.negocore.domain.model.Provider;

import java.util.List;

public interface IProviderPersistencePort {

    Provider save(Provider provider);
    List<Provider> findAllByBusinessId(Long businessId);
}
