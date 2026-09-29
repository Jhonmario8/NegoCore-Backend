package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.DebtCreateRequestDTO;
import com.negocore.application.dto.request.LoanRequestDTO;
import com.negocore.application.dto.response.DebtListResponseDTO;
import com.negocore.application.dto.response.DebtResponseDTO;
import com.negocore.application.handler.IDebtHandler;
import com.negocore.domain.model.DebtStatus;
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
}
