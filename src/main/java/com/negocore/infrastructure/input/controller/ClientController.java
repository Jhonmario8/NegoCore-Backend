package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.ClientRequestDTO;
import com.negocore.application.dto.request.ClientUpdateDTO;
import com.negocore.application.dto.response.ClientResponseDTO;
import com.negocore.application.handler.IClientHandler;
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
public class ClientController {

    private final IClientHandler clientHandler;

    @PostMapping("/{businessId}/clients")
    public ResponseEntity<ClientResponseDTO> registerClient(@PathVariable Long businessId, @Valid @RequestBody ClientRequestDTO clientRequestDTO) {
        ClientResponseDTO clientResponseDTO = clientHandler.registerClient(businessId, clientRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientResponseDTO);
    }

    @GetMapping("/{businessId}/clients")
    public ResponseEntity<List<ClientResponseDTO>> getClientsByBusinessId(@PathVariable Long businessId) {
        return ResponseEntity.ok(clientHandler.getClientsByBusinessId(businessId));
    }

    @PatchMapping("/{businessId}/clients/{clientId}")
    public ResponseEntity<ClientResponseDTO> updateClient(@PathVariable Long businessId, @PathVariable Long clientId, @Valid @RequestBody ClientUpdateDTO clientUpdateDTO) {
        ClientResponseDTO clientResponseDTO = clientHandler.updateClient(businessId, clientId, clientUpdateDTO);
        return ResponseEntity.ok(clientResponseDTO);
    }
}
