package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.Provider;
import com.negocore.domain.spi.IProviderPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.IProviderEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IProviderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProviderJpaAdapter implements IProviderPersistencePort {

    private final IProviderRepository repository;
    private final IProviderEntityMapper mapper;

    @Override
    public Provider save(Provider provider) {

        return mapper.toDomain(
                repository.save(
                        mapper.toEntity(provider)
                )
        );
    }

    @Override
    public List<Provider> findAllByBusinessId(Long businessId) {

        return repository.findAllByBusinessId(businessId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Provider> findByIdAndBusinessId(Long providerId, Long businessId) {

        return repository.findByIdAndBusinessId(providerId, businessId)
                .map(mapper::toDomain);
    }
}
