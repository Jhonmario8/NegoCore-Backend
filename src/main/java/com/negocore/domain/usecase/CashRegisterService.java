package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.ICashRegisterServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.ConflictException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.*;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.domain.spi.ICashMovementPersistencePort;
import com.negocore.domain.spi.ICashRegisterPersistencePort;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class CashRegisterService implements ICashRegisterServicePort {

    private final ICashRegisterPersistencePort cashRegisterPersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final ICashMovementPersistencePort cashMovementPersistencePort;
    @Override
    public CashRegister openCashRegister(Long businessId, BigDecimal openingAmount) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }
        if (cashRegisterPersistencePort.existsOpenCashRegisterByBusinessIdAndStatus(businessId, CashRegisterStatus.OPEN)) {
            throw new IllegalStateException(DomainConstants.CASH_REGISTER_ALREADY_OPEN);
        }
        CashRegister cashRegister = new CashRegister();
        cashRegister.setBusinessId(businessId);
        cashRegister.setOpeningAmount(openingAmount);
        cashRegister.setOpeningAt(LocalDateTime.now());
        cashRegister.setStatus(CashRegisterStatus.OPEN);
        return cashRegisterPersistencePort.save(cashRegister);
    }

    @Override
    @Transactional
    public CashRegisterResponse closeCashRegister(Long businessId, Long cashRegisterId, BigDecimal closingAmount) {

        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!userId.equals(business.getOwnerId())) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }
        CashRegister cashRegister = cashRegisterPersistencePort.findById(cashRegisterId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.CASH_REGISTER_NOT_FOUND));
        if (cashRegister.getStatus() == CashRegisterStatus.CLOSED) {
            throw new ConflictException(DomainConstants.CASH_REGISTER_ALREADY_CLOSED);
        }
        if (cashRegister.getStatus() != CashRegisterStatus.OPEN || !cashRegister.getBusinessId().equals(businessId)) {
            throw new NotFoundException(DomainConstants.CASH_REGISTER_NOT_FOUND);
        }
        List<CashMovement> cashMovementsIn = cashMovementPersistencePort
                .findByCashRegisterIdAndTypeIn(cashRegister.getId(), List.of(CashMovementType.SALE, CashMovementType.MANUAL_IN));
        List<CashMovement> cashMovementsOut = cashMovementPersistencePort
                .findByCashRegisterIdAndTypeIn(cashRegister.getId(), List.of(CashMovementType.EXPENSE, CashMovementType.MANUAL_OUT));

        BigDecimal totalMovements = cashMovementsIn.stream()
                .map(CashMovement::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .subtract(cashMovementsOut.stream()
                        .map(CashMovement::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        BigDecimal expectedAmount = cashRegister.getOpeningAmount().add(totalMovements);
        BigDecimal difference = closingAmount.subtract(expectedAmount);

        cashRegister.setExpectedAmount(expectedAmount);
        cashRegister.setClosingAmount(closingAmount);
        cashRegister.setClosingAt(LocalDateTime.now());
        cashRegister.setStatus(CashRegisterStatus.CLOSED);
        cashRegisterPersistencePort.save(cashRegister);


        return new CashRegisterResponse(
                cashRegister.getExpectedAmount(),
                cashRegister.getClosingAmount(),
                difference
        );
    }
}
