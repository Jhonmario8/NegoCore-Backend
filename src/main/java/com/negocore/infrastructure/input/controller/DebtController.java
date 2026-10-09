package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.DebtCreateRequestDTO;
import com.negocore.application.dto.request.LoanRequestDTO;
import com.negocore.application.dto.response.DebtListResponseDTO;
import com.negocore.application.dto.response.DebtPaymentDTO;
import com.negocore.application.dto.response.DebtResponseDTO;
import com.negocore.application.handler.IDebtHandler;
import com.negocore.domain.model.DebtStatus;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/businesses")
@RequiredArgsConstructor
public class DebtController {

    private final IDebtHandler debtHandler;

    @PostMapping("/{businessId}/debts")
    public ResponseEntity<DebtListResponseDTO> registerLoan(
            @PathVariable Long businessId,
            @Valid @RequestBody LoanRequestDTO loanRequestDTO
    ) {
        DebtListResponseDTO debtListResponseDTO = debtHandler.registerLoan(businessId, loanRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(debtListResponseDTO);
    }

    @PostMapping("/{businessId}/debts/{debtId}/payments")
    public ResponseEntity<DebtResponseDTO> createDebtPayment(
            @PathVariable Long businessId,
            @PathVariable Long debtId,
            @Valid @RequestBody DebtCreateRequestDTO debtCreateRequestDTO) {
        DebtResponseDTO debtResponseDTO = debtHandler.createDebt(businessId, debtId, debtCreateRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(debtResponseDTO);
    }

    @GetMapping("/{businessId}/debts")
    public ResponseEntity<List<DebtListResponseDTO>> findDebts(
            @PathVariable Long businessId,
            @RequestParam(required = false) DebtStatus status,
            @RequestParam(required = false) Long clientId
    ) {

        return ResponseEntity.ok(
                debtHandler.findDebts(
                        businessId,
                        status,
                        clientId
                )
        );
    }

    @GetMapping("/{businessId}/debts/grouped")
    public ResponseEntity<List<DebtListResponseDTO>> findGroupedDebts(
            @PathVariable Long businessId,
            @RequestParam(required = false) DebtStatus status,
            @RequestParam(required = false) Long clientId
    ) {

        return ResponseEntity.ok(
                debtHandler.findGroupedDebts(
                        businessId,
                        status,
                        clientId
                )
        );
    }

    @PostMapping("/{businessId}/debts/clients/{clientId}/payments")
    public ResponseEntity<List<DebtResponseDTO>> payClientDebt(
            @PathVariable Long businessId,
            @PathVariable Long clientId,
            @Valid @RequestBody DebtCreateRequestDTO debtCreateRequestDTO
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(debtHandler.payClientDebt(businessId, clientId, debtCreateRequestDTO));
    }

    @GetMapping("/{businessId}/debts/clients/{clientId}/payments")
    public ResponseEntity<List<DebtPaymentDTO>> findClientDebtPayments(
            @PathVariable Long businessId,
            @PathVariable Long clientId
    ) {
        return ResponseEntity.ok(debtHandler.findClientDebtPayments(businessId, clientId));
    }

    @PatchMapping("/{businessId}/debts/clients/{clientId}/payments/{paymentId}")
    public ResponseEntity<DebtResponseDTO> updateClientDebtPayment(
            @PathVariable Long businessId,
            @PathVariable Long clientId,
            @PathVariable Long paymentId,
            @Valid @RequestBody DebtCreateRequestDTO debtCreateRequestDTO
    ) {
        return ResponseEntity.ok(debtHandler.updateClientDebtPayment(businessId, clientId, paymentId, debtCreateRequestDTO));
    }

    @GetMapping("/{businessId}/debts/{debtId}/payments")
    public ResponseEntity<List<DebtPaymentDTO>> findPaymentsByDebt(
            @PathVariable Long businessId,
            @PathVariable Long debtId
    ) {
        return ResponseEntity.ok(debtHandler.findPaymentsByDebt(businessId, debtId));
    }

    @PatchMapping("/{businessId}/debts/{debtId}/payments/{paymentId}")
    public ResponseEntity<DebtResponseDTO> updateDebtPayment(
            @PathVariable Long businessId,
            @PathVariable Long debtId,
            @PathVariable Long paymentId,
            @Valid @RequestBody DebtCreateRequestDTO debtCreateRequestDTO
    ) {
        return ResponseEntity.ok(debtHandler.updateDebtPayment(businessId, debtId, paymentId, debtCreateRequestDTO));
    }
}
