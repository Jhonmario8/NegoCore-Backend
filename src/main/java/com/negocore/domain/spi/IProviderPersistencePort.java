package com.negocore.domain.spi;

import com.negocore.domain.model.Provider;

import java.util.List;
import java.util.Optional;

public interface IProviderPersistencePort {

    Provider save(Provider provider);
    List<Provider> findAllByBusinessId(Long businessId);
    Optional<Provider> findByIdAndBusinessId(Long providerId, Long businessId);
}
