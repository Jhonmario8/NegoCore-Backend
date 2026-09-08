package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IBalanceReportServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.BalanceReport;
import com.negocore.domain.model.Business;
import com.negocore.domain.model.SaleStatus;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.domain.spi.IExpensePersistencePort;
import com.negocore.domain.spi.ISalePersistencePort;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class BalanceReportService implements IBalanceReportServicePort {

    private final IAuthenticationServicePort authenticationServicePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final ISalePersistencePort salePersistencePort;
    private final IExpensePersistencePort expensePersistencePort;

    @Override
    public BalanceReport getBalanceReport(Long businessId, LocalDate from, LocalDate to) {

        Long userId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() ->
                        new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        if (from == null || to == null) {
            throw new BadRequestException(DomainConstants.REPORT_DATES_REQUIRED);
        }

        if (from.isAfter(to)) {
            throw new BadRequestException(DomainConstants.REPORT_FROM_AFTER_TO);
        }

        LocalDateTime fromDateTime = from.atStartOfDay();
        LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();

        BigDecimal totalSales = salePersistencePort
                .sumTotalByBusinessIdAndCreatedAtBetweenAndStatusNot(
                        businessId,
                        fromDateTime,
                        toDateTime,
                        SaleStatus.CANCELLED
                );

        BigDecimal totalExpenses = expensePersistencePort
                .sumAmountByBusinessIdAndCreatedAtBetween(
                        businessId,
                        fromDateTime,
                        toDateTime
                );

        Long salesCount = salePersistencePort
                .countByBusinessIdAndCreatedAtBetweenAndStatusNot(
                        businessId,
                        fromDateTime,
                        toDateTime,
                        SaleStatus.CANCELLED
                );

        BigDecimal profit = totalSales.subtract(totalExpenses);

        return new BalanceReport(
                totalSales,
                totalExpenses,
                profit,
                salesCount
        );
    }
}