package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IPayableServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.ConflictException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.*;
import com.negocore.domain.spi.*;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class PayableService implements IPayableServicePort {

    private final IPayablePersistencePort payablePersistencePort;
    private final IPayablePaymentPersistencePort payablePaymentPersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final IAuditLogsPersistencePort auditLogsPersistencePort;

    @Override
    public PayablePaymentResponse createPayablePayment(
            Long businessId,
            Long payableId,
            PayablePaymentRequest payablePaymentRequest
    ) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Payable payable = payablePersistencePort.findById(payableId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.PAYABLE_NOT_FOUND));

        if (!payable.getBusinessId().equals(businessId)) {
            throw new NotFoundException(DomainConstants.PAYABLE_NOT_FOUND);
        }

        if (payable.getStatus() == PayableStatus.PAID) {
            throw new ConflictException(DomainConstants.PAYABLE_ALREADY_PAID);
        }
        if (payable.getStatus() == PayableStatus.CANCELLED) {
            throw new ConflictException(DomainConstants.PAYABLE_CANCELLED);
        }

        if (payablePaymentRequest.amount() == null || payablePaymentRequest.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(DomainConstants.PAYMENT_AMOUNT_INVALID);
        }

        BigDecimal pendingAmount = payable.getTotalAmount().subtract(payable.getPaidAmount());

        if (payablePaymentRequest.amount().compareTo(pendingAmount) > 0) {
            throw new BadRequestException(DomainConstants.PAYABLE_AMOUNT_EXCEEDS_TOTAL);
        }

        PayablePayment payment = new PayablePayment();
        payment.setPayableId(payableId);
        payment.setAmount(payablePaymentRequest.amount());
        payment.setPaymentMethod(payablePaymentRequest.paymentMethod());
        payment.setCreatedAt(LocalDateTime.now());
        PayablePayment savedPayment = payablePaymentPersistencePort.save(payment);

        payable.setPaidAmount(payable.getPaidAmount().add(payablePaymentRequest.amount()));
        if (payable.getTotalAmount().equals(payable.getPaidAmount())) {
            payable.setStatus(PayableStatus.PAID);
        } else {
            payable.setStatus(PayableStatus.PARTIAL);
        }
        payablePersistencePort.save(payable);

        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessId(businessId);
        auditLog.setUserId(userId);
        auditLog.setAction(DomainConstants.PAYABLE_PAYMENT_REGISTERED);
        auditLog.setEntity(DomainConstants.PAYABLE_ENTITY);
        auditLog.setEntityId(payableId);
        auditLog.setDetails(DomainConstants.PAYABLE_PAYMENT_REGISTERED_DETAILS + savedPayment.getId());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogsPersistencePort.save(auditLog);

        return new PayablePaymentResponse(savedPayment, payable.getStatus());
    }

    @Override
    public List<Payable> findPayables(
            Long businessId,
            PayableStatus status,
            PayeeType payeeType,
            Long providerId
    ) {
        Long userId = authenticationServicePort.getCurrentUserId();

        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        return payablePersistencePort.findAllByFilters(
                businessId,
                status,
                payeeType,
                providerId
        );
    }

    @Override
    public List<PayablePayment> findPaymentsByPayable(Long businessId, Long payableId) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Payable payable = payablePersistencePort.findById(payableId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.PAYABLE_NOT_FOUND));
        if (!payable.getBusinessId().equals(businessId)) {
            throw new NotFoundException(DomainConstants.PAYABLE_NOT_FOUND);
        }

        return payablePaymentPersistencePort.findAllByPayableId(payableId);
    }

    @Override
    public PayablePaymentResponse updatePayablePayment(
            Long businessId,
            Long payableId,
            Long paymentId,
            PayablePaymentRequest updateRequest
    ) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Payable payable = payablePersistencePort.findById(payableId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.PAYABLE_NOT_FOUND));
        if (!payable.getBusinessId().equals(businessId)) {
            throw new NotFoundException(DomainConstants.PAYABLE_NOT_FOUND);
        }
        if (payable.getStatus() == PayableStatus.CANCELLED) {
            throw new ConflictException(DomainConstants.PAYABLE_CANCELLED);
        }

        if (updateRequest.amount() == null || updateRequest.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(DomainConstants.PAYMENT_AMOUNT_INVALID);
        }

        List<PayablePayment> payments = payablePaymentPersistencePort.findAllByPayableId(payableId);
        PayablePayment target = payments.stream()
                .filter(p -> p.getId().equals(paymentId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(DomainConstants.PAYABLE_PAYMENT_NOT_FOUND));

        BigDecimal newPaidAmount = payments.stream()
                .map(p -> p.getId().equals(paymentId) ? updateRequest.amount() : p.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (newPaidAmount.compareTo(payable.getTotalAmount()) > 0) {
            throw new BadRequestException(DomainConstants.PAYABLE_AMOUNT_EXCEEDS_TOTAL);
        }

        target.setAmount(updateRequest.amount());
        target.setPaymentMethod(updateRequest.paymentMethod());
        PayablePayment savedPayment = payablePaymentPersistencePort.save(target);

        payable.setPaidAmount(newPaidAmount);
        if (newPaidAmount.compareTo(BigDecimal.ZERO) == 0) {
            payable.setStatus(PayableStatus.PENDING);
        } else if (newPaidAmount.compareTo(payable.getTotalAmount()) >= 0) {
            payable.setStatus(PayableStatus.PAID);
        } else {
            payable.setStatus(PayableStatus.PARTIAL);
        }
        payablePersistencePort.save(payable);

        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessId(businessId);
        auditLog.setUserId(userId);
        auditLog.setAction(DomainConstants.PAYABLE_PAYMENT_UPDATED);
        auditLog.setEntity(DomainConstants.PAYABLE_ENTITY);
        auditLog.setEntityId(payableId);
        auditLog.setDetails(DomainConstants.PAYABLE_PAYMENT_UPDATED_DETAILS + savedPayment.getId());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogsPersistencePort.save(auditLog);

        return new PayablePaymentResponse(savedPayment, payable.getStatus());
    }
}
