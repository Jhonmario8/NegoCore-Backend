package com.negocore.domain.spi;

import com.negocore.domain.model.User;

import java.util.Optional;

public interface IUserPersistencePort {

    User saveUser(User user);
    Optional<User> findByEmail(String email);
    Boolean existsByPhoneNumber(String phoneNumber);

}
