package com.negocore.domain.usecase;

import com.negocore.domain.api.IPasswordServicePort;
import com.negocore.domain.api.IUserServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.ConflictException;
import com.negocore.domain.model.User;
import com.negocore.domain.spi.IUserPersistencePort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class UserService implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IPasswordServicePort passwordServicePort;

    @Override
    public User createUser(User user) {
        user.validate();
        validateUniqueness(user);
        user.encodePassword(passwordServicePort);
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);
        return userPersistencePort.saveUser(user);
    }

    private void validateUniqueness(User user) {

        if (user.getEmail() != null && user.getPhoneNumber() != null) {
            if (userPersistencePort.findByEmail(user.getEmail()).isPresent()) {
                throw new ConflictException(DomainConstants.EMAIL_ALREADY_EXISTS);
            }

            if (userPersistencePort.existsByPhoneNumber(user.getPhoneNumber())) {
                throw new ConflictException(DomainConstants.PHONE_NUMBER_ALREADY_EXISTS);
            }


        }
    }
}