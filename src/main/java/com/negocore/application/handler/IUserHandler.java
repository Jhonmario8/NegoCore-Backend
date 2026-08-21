package com.negocore.application.handler;

import com.negocore.application.dto.UserDTO;
import com.negocore.application.dto.UserResponseDTO;

public interface IUserHandler {

    UserResponseDTO registerUser(UserDTO userDTO);
}
