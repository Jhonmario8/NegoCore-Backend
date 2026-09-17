package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IDebtServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.ConflictException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.*;
import com.negocore.domain.spi.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DebtService implements IDebtServicePort {

    private final IDebtPersistencePort debtPersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final IDebtPaymentPersistencePort debtPaymentPersistencePort;
    private final IAuditLogsPersistencePort auditLogsPersistencePort;
    private final IClientPersistencePort clientPersistencePort;

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

        if (!debt.getBusinessId().equals(businessId)) {
            throw new NotFoundException(DomainConstants.DEBT_NOT_FOUND);
        }

        if (debt.getStatus() == DebtStatus.PAID) {
            throw new ConflictException(DomainConstants.DEBT_ALREADY_PAID);
        }
        if (debt.getStatus() == DebtStatus.CANCELLED) {
            throw new ConflictException(DomainConstants.DEBT_CANCELLED);
        }

        BigDecimal pendingAmount = debt.getTotalAmount()
                .subtract(debt.getPaidAmount());

        if (debtCreateRequest.amount().compareTo(pendingAmount) > 0) {
            throw new BadRequestException(
                    DomainConstants.DEBT_AMOUNT_EXCEEDS_TOTAL
            );
        }


        DebtPayment payment = new DebtPayment();
        payment.setDebtId(debtId);
        payment.setAmount(debtCreateRequest.amount());
        payment.setPaymentMethod(debtCreateRequest.paymentMethod());
        payment.setCreatedAt(LocalDateTime.now());
        DebtPayment savedPayment = debtPaymentPersistencePort.save(payment);

        debt.setPaidAmount(debt.getPaidAmount().add(debtCreateRequest.amount()));
        if (debt.getTotalAmount().equals(debt.getPaidAmount())) {
            debt.setStatus(DebtStatus.PAID);
        } else {
            debt.setStatus(DebtStatus.PARTIAL);
        }
        debtPersistencePort.save(debt);
       

        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessId(businessId);
        auditLog.setUserId(userId);
        auditLog.setAction(DomainConstants.DEBT_PAYMENT_REGISTERED);
        auditLog.setEntity(DomainConstants.DEBT_ENTITY);
        auditLog.setEntityId(debtId);
        auditLog.setDetails(DomainConstants.DEBT_PAYMENT_REGISTERED_DETAILS + savedPayment.getId());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogsPersistencePort.save(auditLog);

        return new DebtPaymentResponse(savedPayment, debt.getStatus());
    }

    @Override
    public List<Debt> findDebts(
            Long businessId,
            DebtStatus status,
            Long clientId
    ) {

        Long userId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() ->
                        new NotFoundException(
                                DomainConstants.BUSINESS_NOT_FOUND
                        )
                );

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(
                    DomainConstants.BUSINESS_NOT_FOUND
            );
        }

        return debtPersistencePort.findAllByFilters(
                businessId,
                status,
                clientId
        );
    }

    @Override
    public Debt registerLoan(Long businessId, Debt loan) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        if (loan.getClientId() != null) {
            clientPersistencePort.findByIdAndBusinessId(loan.getClientId(), businessId)
                    .orElseThrow(() -> new NotFoundException(DomainConstants.CLIENT_NOT_FOUND));
            loan.setDebtorName(null);
        } else if (loan.getDebtorName() == null || loan.getDebtorName().isBlank()) {
            throw new BadRequestException(DomainConstants.DEBTOR_NAME_REQUIRED_FOR_NO_CLIENT);
        }

        loan.setBusinessId(businessId);
        loan.setPaidAmount(BigDecimal.ZERO);
        loan.setStatus(DebtStatus.PENDING);
        if (loan.getDueDate() == null) {
            loan.setDueDate(LocalDateTime.now().plusDays(30).toLocalDate());
        }
        loan.setCreatedAt(LocalDateTime.now());

        Debt savedLoan = debtPersistencePort.save(loan);

        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessId(businessId);
        auditLog.setUserId(userId);
        auditLog.setAction(DomainConstants.DEBT_CREATED);
        auditLog.setEntity(DomainConstants.DEBT_ENTITY);
        auditLog.setEntityId(savedLoan.getId());
        auditLog.setDetails(DomainConstants.DEBT_CREATED_DETAILS + savedLoan.getId());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogsPersistencePort.save(auditLog);

        return savedLoan;
    }

}
