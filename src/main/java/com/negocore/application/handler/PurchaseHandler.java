package com.negocore.application.handler;

import com.negocore.application.dto.request.DateUpdateDTO;
import com.negocore.application.dto.request.PurchaseRequestDTO;
import com.negocore.application.dto.response.PurchaseListResponseDTO;
import com.negocore.application.dto.response.PurchaseResponseDTO;
import com.negocore.application.mapper.IPurchaseMapper;
import com.negocore.domain.api.IPurchaseServicePort;
import com.negocore.domain.model.PurchaseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PurchaseHandler implements IPurchaseHandler {

    private final IPurchaseServicePort purchaseServicePort;
    private final IPurchaseMapper purchaseMapper;

    @Override
    public PurchaseResponseDTO registerPurchase(
            Long businessId,
            PurchaseRequestDTO purchaseRequestDTO
    ) {
        return purchaseMapper.toResponseDto(
                purchaseServicePort.registerPurchase(
                        businessId,
                        purchaseMapper.toDomain(purchaseRequestDTO)
                )
        );
    }

    @Override
    public PurchaseResponseDTO updatePurchaseDate(
            Long businessId,
            Long purchaseId,
            DateUpdateDTO dateUpdateDTO
    ) {
        return purchaseMapper.toResponseDto(
                purchaseServicePort.updatePurchaseDate(
                        businessId,
                        purchaseId,
                        dateUpdateDTO.getCreatedAt()
                )
        );
    }

    @Override
    public List<PurchaseListResponseDTO> findPurchases(
            Long businessId,
            Long providerId,
            PurchaseStatus status,
            LocalDate from,
            LocalDate to
    ) {
        LocalDateTime fromDateTime =
                from != null
                        ? from.atStartOfDay()
                        : null;

        LocalDateTime toDateTime =
                to != null
                        ? to.plusDays(1).atStartOfDay()
                        : null;

        return purchaseServicePort.findPurchases(
                        businessId,
                        providerId,
                        status,
                        fromDateTime,
                        toDateTime
                )
                .stream()
                .map(purchaseMapper::toListResponseDto)
                .toList();
    }

    @Override
    public PurchaseResponseDTO findPurchaseById(
            Long businessId,
            Long purchaseId
    ) {
        return purchaseMapper.toResponseDto(
                purchaseServicePort.findPurchaseById(
                        businessId,
                        purchaseId
                )
        );
    }
}
