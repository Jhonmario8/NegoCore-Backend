package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.Business;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.IBusinessEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IBusinessRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BusinessJpaAdapter implements IBusinessPersistencePort {

    private final IBusinessRepository repository;
    private final IBusinessEntityMapper mapper;

    @Override
    public Business saveBusiness(Business business) {
        return mapper.toDomain(repository.save(mapper.toEntity(business)));
    }

    @Override
    public List<Business> findAllByOwnerId(Long ownerId) {
        return repository.findByOwnerId(ownerId).stream().map(mapper::toDomain).toList();
    }
}
