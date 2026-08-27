package com.negocore.application.handler;

import com.negocore.application.dto.request.LoginDTO;
import com.negocore.application.dto.request.UserDTO;
import com.negocore.application.dto.response.UserResponseDTO;
import com.negocore.application.mapper.IUserMapper;
import com.negocore.domain.api.IUserServicePort;
import com.negocore.domain.model.LoginResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserHandler implements IUserHandler{

    private final IUserServicePort userServicePort;
    private final IUserMapper mapper;

    @Override
    public UserResponseDTO registerUser(UserDTO userDTO) {
        return mapper.toResponseDTO(userServicePort.createUser(mapper.toDomain(userDTO)));
    }

    @Override
    public LoginResponse loginUser(LoginDTO loginDTO) {
        return userServicePort.login(loginDTO.getEmail(), loginDTO.getPassword());
    }
}
