package com.negocore.domain.api;

import com.negocore.domain.model.BalanceReport;

import java.time.LocalDate;

public interface IBalanceReportServicePort {

    BalanceReport getBalanceReport(
            Long businessId,
            LocalDate from,
            LocalDate to
    );

}