package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.QuoteRequestDTO;
import com.negocore.application.dto.response.QuoteResponseDTO;
import com.negocore.application.handler.IQuoteHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/businesses")
@RequiredArgsConstructor
public class QuoteController {

    private final IQuoteHandler quoteHandler;

    @PostMapping("/{businessId}/quotes")
    public ResponseEntity<QuoteResponseDTO> generateQuote(@PathVariable Long businessId, @Valid @RequestBody QuoteRequestDTO quoteRequestDTO) {
        QuoteResponseDTO quoteResponseDTO = quoteHandler.generateQuote(businessId, quoteRequestDTO);
        return ResponseEntity.ok(quoteResponseDTO);
    }
}
