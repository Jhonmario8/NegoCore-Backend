package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.Client;
import com.negocore.domain.spi.IClientPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.IClientEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientJpaAdapter implements IClientPersistencePort {

    private final IClientRepository repository;
    private final IClientEntityMapper mapper;

    @Override
    public Client save(Client client) {
        return mapper.toDomain(repository.save(mapper.toEntity(client)));
    }

    @Override
    public List<Client> findAllByBusinessId(Long businessId) {
        return repository.findAllByBusinessId(businessId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Client> findByIdAndBusinessId(Long clientId, Long businessId) {
        return repository.findByIdAndBusinessId(clientId, businessId)
                .map(mapper::toDomain);
    }
}
