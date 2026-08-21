package com.negocore.domain.model;

import com.negocore.domain.api.IPasswordServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private static final String PASSWORD_PATTERN = "^(?=.*[A-Z])(?=.*\\d).{8,}$";
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    private static final String PHONE_NUMBER_PATTERN = "^\\+?[0-9]{10,15}$";

    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private String password;
    private boolean active;
    private LocalDateTime createdAt;

    public void encodePassword(IPasswordServicePort passwordServicePort) {
        this.password = passwordServicePort.encodePassword(this.password);
    }

    public void validate() {
        if (!password.matches(PASSWORD_PATTERN)) {
            throw new BadRequestException(DomainConstants.INVALID_PASSWORD_MESSAGE);
        }
        if (!email.matches(EMAIL_PATTERN)) {
            throw new BadRequestException(DomainConstants.INVALID_EMAIL_MESSAGE);
        }
        if (!phoneNumber.matches(PHONE_NUMBER_PATTERN)) {
            throw new BadRequestException(DomainConstants.INVALID_PHONE_NUMBER_MESSAGE);
        }

    }


}
