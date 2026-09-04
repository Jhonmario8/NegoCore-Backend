package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IDebtServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.*;
import com.negocore.domain.spi.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DebtService implements IDebtServicePort {

    private final IDebtPersistencePort debtPersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final IDebtPaymentPersistencePort debtPaymentPersistencePort;
    private final ICashRegisterPersistencePort cashRegisterPersistencePort;
    private final ICashMovementPersistencePort cashMovementPersistencePort;

    @Override
    @Transactional
    public DebtPaymentResponse createDebt(Long businessId, Long debtId, DebtCreateRequest debtCreateRequest) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }
        Debt debt = debtPersistencePort.findById(debtId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.DEBT_NOT_FOUND));

        BigDecimal pendingAmount = debt.getTotalAmount()
                .subtract(debt.getPaidAmount());

        if (debtCreateRequest.amount().compareTo(pendingAmount) > 0) {
            throw new BadRequestException(
                    DomainConstants.DEBT_AMOUNT_EXCEEDS_TOTAL
            );
        }
        if (!debt.getBusinessId().equals(businessId)){
            throw new NotFoundException(DomainConstants.DEBT_NOT_FOUND);
        }
        Optional<CashRegister> cashRegister = cashRegisterPersistencePort
                .findOpenCashRegisterByBusinessIdAndStatus(businessId, CashRegisterStatus.OPEN);

        DebtPayment payment = new DebtPayment();
        payment.setDebtId(debtId);
        payment.setAmount(debtCreateRequest.amount());
        payment.setPaymentMethod(debtCreateRequest.paymentMethod());
        payment.setCreatedAt(LocalDateTime.now());
        cashRegister.ifPresent(register -> payment.setCashRegisterId(register.getId()));
        DebtPayment savedPayment = debtPaymentPersistencePort.save(payment);

        debt.setPaidAmount(debt.getPaidAmount().add(debtCreateRequest.amount()));
        if (debt.getTotalAmount().equals(debt.getPaidAmount())) {
            debt.setStatus(DebtStatus.PAID);
        } else {
            debt.setStatus(DebtStatus.PARTIAL);
        }

        if (debtCreateRequest.paymentMethod() == DebtPaymentMethod.CASH) {

            if (cashRegister.isPresent()){
                CashMovement cashMovement = new CashMovement();
                cashMovement.setCashRegisterId(cashRegister.get().getId());
                cashMovement.setType(CashMovementType.MANUAL_IN);
                cashMovement.setAmount(debtCreateRequest.amount());
                cashMovement.setDescription(DomainConstants.CASH_MOVEMENT_DEBT_PAYMENT);
                cashMovement.setReferenceId(savedPayment.getId());
                cashMovement.setCreatedAt(LocalDateTime.now());
                cashMovementPersistencePort.save(cashMovement);
            }
        }
        return new DebtPaymentResponse(savedPayment, debt.getStatus());
    }
}
