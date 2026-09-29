package com.negocore.domain.usecase;

import com.negocore.domain.api.IPasswordServicePort;
import com.negocore.domain.api.ITokenServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.ConflictException;
import com.negocore.domain.exception.ForbiddenException;
import com.negocore.domain.exception.UnauthorizedException;
import com.negocore.domain.model.LoginResponse;
import com.negocore.domain.model.User;
import com.negocore.domain.spi.IUserPersistencePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private IUserPersistencePort userPersistencePort;
    @Mock private IPasswordServicePort passwordServicePort;
    @Mock private ITokenServicePort tokenServicePort;

    @InjectMocks private UserService userService;

    private User aValidUser() {
        User user = new User();
        user.setName("Jhon");
        user.setEmail("jhon@example.com");
        user.setPhoneNumber("3001234567");
        user.setPassword("Password1");
        return user;
    }

    @Test
    @DisplayName("Registrar con un email ya existente lanza ConflictException")
    void createUser_duplicateEmail_throwsConflict() {
        User user = aValidUser();
        when(userPersistencePort.findByEmail(user.getEmail())).thenReturn(Optional.of(new User()));

        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(ConflictException.class)
                .hasMessage(DomainConstants.EMAIL_ALREADY_EXISTS);

        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Registrar con un teléfono ya existente lanza ConflictException")
    void createUser_duplicatePhone_throwsConflict() {
        User user = aValidUser();
        when(userPersistencePort.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userPersistencePort.existsByPhoneNumber(user.getPhoneNumber())).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(ConflictException.class)
                .hasMessage(DomainConstants.PHONE_NUMBER_ALREADY_EXISTS);

        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Una contraseña con formato inválido lanza BadRequestException")
    void createUser_invalidPassword_throwsBadRequest() {
        User user = aValidUser();
        user.setPassword("short");

        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.INVALID_PASSWORD_MESSAGE);

        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Un email con formato inválido lanza BadRequestException")
    void createUser_invalidEmail_throwsBadRequest() {
        User user = aValidUser();
        user.setEmail("no-es-un-email");

        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.INVALID_EMAIL_MESSAGE);
    }

    @Test
    @DisplayName("Un teléfono con formato inválido lanza BadRequestException")
    void createUser_invalidPhoneNumber_throwsBadRequest() {
        User user = aValidUser();
        user.setPhoneNumber("123");

        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.INVALID_PHONE_NUMBER_MESSAGE);
    }

    @Test
    @DisplayName("Registrar con password nulo lanza BadRequestException")
    void createUser_nullPassword_throwsBadRequest() {
        User user = aValidUser();
        user.setPassword(null);

        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.PASSWORD_NULL_OR_EMPTY);

        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Registrar con password vacío lanza BadRequestException")
    void createUser_blankPassword_throwsBadRequest() {
        User user = aValidUser();
        user.setPassword("   ");

        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.PASSWORD_NULL_OR_EMPTY);
    }

    @Test
    @DisplayName("Registrar con email nulo lanza BadRequestException")
    void createUser_nullEmail_throwsBadRequest() {
        User user = aValidUser();
        user.setEmail(null);

        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.EMAIL_NULL_OR_EMPTY);

        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Registrar con teléfono nulo lanza BadRequestException, porque el teléfono es obligatorio")
    void createUser_nullPhoneNumber_throwsBadRequest() {
        User user = aValidUser();
        user.setPhoneNumber(null);

        assertThatThrownBy(() -> userService.createUser(user))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.PHONE_NUMBER_NULL_OR_EMPTY);

        verify(userPersistencePort, never()).saveUser(any());
    }

    @Test
    @DisplayName("Un registro válido codifica la contraseña y guarda al usuario activo")
    void createUser_valid_encodesPasswordAndSaves() {
        User user = aValidUser();
        when(userPersistencePort.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userPersistencePort.existsByPhoneNumber(user.getPhoneNumber())).thenReturn(false);
        when(passwordServicePort.encodePassword("Password1")).thenReturn("hashed-password");
        when(userPersistencePort.saveUser(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User saved = userService.createUser(user);

        assertThat(saved.getPassword()).isEqualTo("hashed-password");
        assertThat(saved.isActive()).isTrue();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Login con un usuario inexistente lanza UnauthorizedException por credenciales inválidas")
    void login_userNotFound_throwsInvalidCredentials() {
        when(userPersistencePort.findByEmail("nadie@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.login("nadie@example.com", "Password1"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(DomainConstants.INVALID_CREDENTIALS);
    }

    @Test
    @DisplayName("Login con contraseña incorrecta lanza UnauthorizedException por credenciales inválidas")
    void login_wrongPassword_throwsInvalidCredentials() {
        User user = aValidUser();
        user.setPassword("hashed-password");
        when(userPersistencePort.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordServicePort.matches("wrong", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> userService.login(user.getEmail(), "wrong"))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage(DomainConstants.INVALID_CREDENTIALS);

        verify(tokenServicePort, never()).generateToken(any());
    }

    @Test
    @DisplayName("Usuario inexistente y contraseña incorrecta dan exactamente el mismo mensaje, para no permitir enumerar usuarios")
    void login_userNotFoundAndWrongPassword_giveIdenticalMessage() {
        when(userPersistencePort.findByEmail("nadie@example.com")).thenReturn(Optional.empty());
        String userNotFoundMessage = catchThrowableOfType(
                () -> userService.login("nadie@example.com", "Password1"), UnauthorizedException.class
        ).getMessage();

        User user = aValidUser();
        user.setPassword("hashed-password");
        when(userPersistencePort.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordServicePort.matches("wrong", "hashed-password")).thenReturn(false);
        String wrongPasswordMessage = catchThrowableOfType(
                () -> userService.login(user.getEmail(), "wrong"), UnauthorizedException.class
        ).getMessage();

        assertThat(userNotFoundMessage).isEqualTo(wrongPasswordMessage);
    }

    @Test
    @DisplayName("Login de un usuario inactivo con la contraseña correcta lanza ForbiddenException")
    void login_inactiveUser_throwsForbidden() {
        User user = aValidUser();
        user.setPassword("hashed-password");
        user.setActive(false);
        when(userPersistencePort.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordServicePort.matches("Password1", "hashed-password")).thenReturn(true);

        assertThatThrownBy(() -> userService.login(user.getEmail(), "Password1"))
                .isInstanceOf(ForbiddenException.class)
                .hasMessage(DomainConstants.USER_INACTIVE);

        verify(tokenServicePort, never()).generateToken(any());
    }

    @Test
    @DisplayName("Un login exitoso retorna el token generado y los datos del usuario")
    void login_success_returnsTokenAndUserInfo() {
        User user = aValidUser();
        user.setId(7L);
        user.setPassword("hashed-password");
        user.setActive(true);
        when(userPersistencePort.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(passwordServicePort.matches("Password1", "hashed-password")).thenReturn(true);
        when(tokenServicePort.generateToken(user)).thenReturn("a-jwt-token");

        LoginResponse response = userService.login(user.getEmail(), "Password1");

        assertThat(response.getToken()).isEqualTo("a-jwt-token");
        assertThat(response.getUserId()).isEqualTo(7L);
        assertThat(response.getUserName()).isEqualTo("Jhon");
        assertThat(response.getPhoneNumber()).isEqualTo("3001234567");
    }
}
