package com.negocore.application.handler;

import com.negocore.application.dto.response.BalanceReportResponseDTO;
import com.negocore.application.mapper.IBalanceReportMapper;
import com.negocore.domain.api.IBalanceReportServicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class BalanceReportHandler implements IBalanceReportHandler {

    private final IBalanceReportServicePort balanceReportServicePort;
    private final IBalanceReportMapper mapper;

    @Override
    public BalanceReportResponseDTO getBalanceReport(
            Long businessId,
            LocalDate from,
            LocalDate to
    ) {

        return mapper.toResponse(
                balanceReportServicePort.getBalanceReport(
                        businessId,
                        from,
                        to
                )
        );
    }
}