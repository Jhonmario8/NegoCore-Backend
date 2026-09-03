package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.DebtPayment;
import com.negocore.infrastructure.output.jpa.entity.DebtPaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IDebtPaymentEntityMapper {

    DebtPaymentEntity toEntity(DebtPayment debtPayment);

    DebtPayment toDomain(DebtPaymentEntity debtPaymentEntity);


}
