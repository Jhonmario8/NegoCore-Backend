package com.negocore.infrastructure.input.controller;

import com.negocore.application.dto.response.AuditLogPageResponseDTO;
import com.negocore.application.handler.IAuditLogHandler;
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
public class AuditLogController {

    private final IAuditLogHandler auditLogHandler;

    @GetMapping("/{businessId}/audit-logs")
    public ResponseEntity<AuditLogPageResponseDTO> findAuditLogs(
            @PathVariable Long businessId,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate from,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate to,

            @RequestParam(required = false)
            String action,

            @RequestParam(required = false)
            String entity,

            @RequestParam(required = false)
            Long entityId,

            @RequestParam(required = false)
            Integer page,

            @RequestParam(required = false)
            Integer size
    ) {

        return ResponseEntity.ok(
                auditLogHandler.findAuditLogs(
                        businessId,
                        from,
                        to,
                        action,
                        entity,
                        entityId,
                        page,
                        size
                )
        );
    }
}
