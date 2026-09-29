package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.PayablePaymentRequestDTO;
import com.negocore.application.dto.response.PayableListResponseDTO;
import com.negocore.application.dto.response.PayablePaymentResponseDTO;
import com.negocore.application.handler.IPayableHandler;
import com.negocore.domain.model.PayableStatus;
import com.negocore.domain.model.PayeeType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/businesses")
@RequiredArgsConstructor
public class PayableController {

    private final IPayableHandler payableHandler;

    @PostMapping("/{businessId}/payables/{payableId}/payments")
    public ResponseEntity<PayablePaymentResponseDTO> createPayablePayment(
            @PathVariable Long businessId,
            @PathVariable Long payableId,
            @Valid @RequestBody PayablePaymentRequestDTO payablePaymentRequestDTO
    ) {
        PayablePaymentResponseDTO payablePaymentResponseDTO =
                payableHandler.createPayablePayment(businessId, payableId, payablePaymentRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(payablePaymentResponseDTO);
    }

    @GetMapping("/{businessId}/payables")
    public ResponseEntity<List<PayableListResponseDTO>> findPayables(
            @PathVariable Long businessId,

            @RequestParam(required = false)
            PayableStatus status,

            @RequestParam(required = false)
            PayeeType payeeType,

            @RequestParam(required = false)
            Long providerId
    ) {
        return ResponseEntity.ok(
                payableHandler.findPayables(
                        businessId,
                        status,
                        payeeType,
                        providerId
                )
        );
    }
}
