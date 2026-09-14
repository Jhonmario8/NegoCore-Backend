package com.negocore.infrastructure.output.jpa.mapper;

import com.negocore.domain.model.PayablePayment;
import com.negocore.infrastructure.output.jpa.entity.PayablePaymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IPayablePaymentEntityMapper {

    PayablePayment toDomain(PayablePaymentEntity payablePaymentEntity);

    PayablePaymentEntity toEntity(PayablePayment payablePayment);
}
