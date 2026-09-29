package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.request.ExpenseRequestDTO;
import com.negocore.application.dto.response.ExpenseResponseDTO;
import com.negocore.application.handler.IExpenseHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/businesses")
@RequiredArgsConstructor
public class ExpenseController {

    private final IExpenseHandler expenseHandler;

    @PostMapping("/{businessId}/expenses")
    public ResponseEntity<ExpenseResponseDTO> registerExpense(@PathVariable Long businessId, @Valid @RequestBody ExpenseRequestDTO expenseRequestDTO) {
        ExpenseResponseDTO expenseResponseDTO = expenseHandler.registerExpense(businessId, expenseRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(expenseResponseDTO);
    }

    @GetMapping("/{businessId}/expenses")
    public ResponseEntity<List<ExpenseResponseDTO>> findExpenses(
            @PathVariable Long businessId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {

        return ResponseEntity.ok(
                expenseHandler.findExpenses(
                        businessId,
                        from,
                        to
                )
        );
    }
}
