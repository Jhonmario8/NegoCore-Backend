package com.negocore.infrastructure.output.jpa.adapter;

import com.negocore.domain.model.User;
import com.negocore.domain.spi.IUserPersistencePort;
import com.negocore.infrastructure.output.jpa.mapper.IUserEntityMapper;
import com.negocore.infrastructure.output.jpa.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserJpaAdapter implements IUserPersistencePort {

    private final IUserRepository repository;
    private final IUserEntityMapper mapper;

    @Override
    public User saveUser(User user) {
        return mapper.toDomain(repository.save(mapper.toEntity(user)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return repository.findByEmail(email).map(mapper::toDomain);
    }


    @Override
    public Boolean existsByPhoneNumber(String phoneNumber) {
        return repository.existsByPhoneNumber(phoneNumber);
    }
}
