package com.negocore.application.handler;

import com.negocore.application.dto.request.LoginDTO;
import com.negocore.application.dto.request.UserDTO;
import com.negocore.application.dto.response.UserResponseDTO;
import com.negocore.domain.model.LoginResponse;

public interface IUserHandler {

    UserResponseDTO registerUser(UserDTO userDTO);
    LoginResponse loginUser(LoginDTO loginDTO);
}
