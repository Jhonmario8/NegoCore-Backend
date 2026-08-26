package com.negocore.domain.usecase;

import com.negocore.domain.api.IPasswordServicePort;
import com.negocore.domain.api.ITokenServicePort;
import com.negocore.domain.api.IUserServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.ConflictException;
import com.negocore.domain.exception.ForbiddenException;
import com.negocore.domain.model.LoginResponse;
import com.negocore.domain.model.User;
import com.negocore.domain.spi.IUserPersistencePort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class UserService implements IUserServicePort {

    private final IUserPersistencePort userPersistencePort;
    private final IPasswordServicePort passwordServicePort;
    private final ITokenServicePort tokenServicePort;

    @Override
    public User createUser(User user) {
        user.validate();
        validateUniqueness(user);
        user.encodePassword(passwordServicePort);
        user.setCreatedAt(LocalDateTime.now());
        user.setActive(true);
        return userPersistencePort.saveUser(user);
    }

    @Override
    public LoginResponse login(String email, String password) {
        User user = userPersistencePort.findByEmail(email)
                .orElseThrow(() -> new ConflictException(DomainConstants.INVALID_CREDENTIALS));

        if (!passwordServicePort.matches(password, user.getPassword())) {
            throw new ConflictException(DomainConstants.INVALID_CREDENTIALS);
        }
        if (!user.isActive()){
            throw new ForbiddenException(DomainConstants.USER_INACTIVE);
        }
        LoginResponse response = new LoginResponse();
        response.setToken(tokenServicePort.generateToken(user));
        response.setUserId(user.getId());
        response.setUserName(user.getName());
        response.setPhoneNumber(user.getPhoneNumber());

        return response;
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