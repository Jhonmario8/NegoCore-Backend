package com.negocore.infrastructure.output.security.adapter;

import com.negocore.domain.api.IPasswordServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PasswordServeAdapter implements IPasswordServicePort {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encodePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new BadRequestException(DomainConstants.PASSWORD_NULL_OR_EMPTY);
        }
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean matches(String password, String hashedPassword) {
        if (password == null || password.isEmpty() || hashedPassword == null || hashedPassword.isEmpty()) {
            throw new BadRequestException(DomainConstants.PASSWORD_NULL_OR_EMPTY);
        }
        return passwordEncoder.matches(password, hashedPassword);
    }
}
