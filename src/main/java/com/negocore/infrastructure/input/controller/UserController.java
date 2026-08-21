package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.UserDTO;
import com.negocore.application.dto.UserResponseDTO;
import com.negocore.application.handler.IUserHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class UserController {

    private final IUserHandler iUserHandler;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@RequestBody UserDTO userDTO) {
        UserResponseDTO userResponseDTO = iUserHandler.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

}
