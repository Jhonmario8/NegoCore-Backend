package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.ProviderCreateRequestDTO;
import com.negocore.application.dto.request.ProviderUpdateDTO;
import com.negocore.application.dto.response.ProviderResponseDTO;
import com.negocore.application.handler.IProviderHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/businesses")
@RequiredArgsConstructor
public class ProviderController {

    private final IProviderHandler providerHandler;

    @PostMapping("/{businessId}/providers")
    public ResponseEntity<ProviderResponseDTO> createProvider(
            @PathVariable Long businessId,
            @Valid @RequestBody ProviderCreateRequestDTO providerCreateRequestDTO
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        providerHandler.createProvider(
                                businessId,
                                providerCreateRequestDTO
                        )
                );
    }

    @GetMapping("/{businessId}/providers")
    public ResponseEntity<List<ProviderResponseDTO>> findAllByBusinessId(
            @PathVariable Long businessId
    ) {

        return ResponseEntity.ok(
                providerHandler.findAllByBusinessId(businessId)
        );
    }

    @PatchMapping("/{businessId}/providers/{providerId}")
    public ResponseEntity<ProviderResponseDTO> updateProvider(
            @PathVariable Long businessId,
            @PathVariable Long providerId,
            @Valid @RequestBody ProviderUpdateDTO providerUpdateDTO
    ) {
        return ResponseEntity.ok(
                providerHandler.updateProvider(businessId, providerId, providerUpdateDTO)
        );
    }
}
