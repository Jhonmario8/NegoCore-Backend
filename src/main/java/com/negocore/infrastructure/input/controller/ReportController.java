package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.response.BalanceReportResponseDTO;
import com.negocore.application.handler.IBalanceReportHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/businesses")
@RequiredArgsConstructor
public class ReportController {

    private final IBalanceReportHandler balanceReportHandler;

    @GetMapping("/{businessId}/reports/balance")
    public ResponseEntity<BalanceReportResponseDTO> getBalanceReport(
            @PathVariable Long businessId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to
    ) {

        return ResponseEntity.ok(
                balanceReportHandler.getBalanceReport(
                        businessId,
                        from,
                        to
                )
        );
    }
}
