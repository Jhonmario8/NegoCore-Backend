package com.negocore.application.mapper;

import com.negocore.application.dto.response.BalanceReportResponseDTO;
import com.negocore.domain.model.BalanceReport;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IBalanceReportMapper {

    BalanceReportResponseDTO toResponse(BalanceReport balanceReport);

}