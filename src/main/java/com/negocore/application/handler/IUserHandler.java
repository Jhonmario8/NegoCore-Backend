package com.negocore.application.handler;

import com.negocore.application.dto.LoginDTO;
import com.negocore.application.dto.UserDTO;
import com.negocore.application.dto.UserResponseDTO;
import com.negocore.domain.model.LoginResponse;

public interface IUserHandler {

    UserResponseDTO registerUser(UserDTO userDTO);
    LoginResponse loginUser(LoginDTO loginDTO);
}
