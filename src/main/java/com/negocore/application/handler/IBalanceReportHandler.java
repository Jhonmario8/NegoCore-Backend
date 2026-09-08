package com.negocore.application.handler;

import com.negocore.application.dto.response.BalanceReportResponseDTO;

import java.time.LocalDate;

public interface IBalanceReportHandler {

    BalanceReportResponseDTO getBalanceReport(
            Long businessId,
            LocalDate from,
            LocalDate to
    );

}