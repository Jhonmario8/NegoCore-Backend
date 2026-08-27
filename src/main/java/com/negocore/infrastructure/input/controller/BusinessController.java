package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.BusinessCreateDTO;
import com.negocore.application.dto.response.BusinessResponseDTO;
import com.negocore.application.handler.IBusinessHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final IBusinessHandler businessHandler;

    @PostMapping()
    public ResponseEntity<BusinessResponseDTO> createBusiness(@Valid @RequestBody BusinessCreateDTO businessCreateDTO) {
        BusinessResponseDTO businessResponseDTO = businessHandler.createBusiness(businessCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(businessResponseDTO);
    }

}
