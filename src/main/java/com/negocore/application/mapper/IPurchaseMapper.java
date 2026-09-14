package com.negocore.application.mapper;

import com.negocore.application.dto.request.PurchaseRequestDTO;
import com.negocore.application.dto.response.PurchaseListResponseDTO;
import com.negocore.application.dto.response.PurchaseResponseDTO;
import com.negocore.domain.model.Purchase;
import com.negocore.domain.model.PurchaseRequest;
import com.negocore.domain.model.PurchaseResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface IPurchaseMapper {

    PurchaseResponseDTO toResponseDto(PurchaseResponse purchaseResponse);
    PurchaseRequest toDomain(PurchaseRequestDTO purchaseRequestDTO);
    PurchaseListResponseDTO toListResponseDto(Purchase purchase);
}
