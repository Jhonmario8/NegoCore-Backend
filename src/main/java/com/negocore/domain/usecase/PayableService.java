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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
public class PayableService implements IPayableServicePort {

    private final IPayablePersistencePort payablePersistencePort;
    private final IPayablePaymentPersistencePort payablePaymentPersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final IAuditLogsPersistencePort auditLogsPersistencePort;
    private final IProviderPersistencePort providerPersistencePort;

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
    public List<Payable> findGroupedPayables(Long businessId, PayableStatus status, Long providerId) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        List<Payable> allPayables = payablePersistencePort.findAllByFilters(businessId, null, null, providerId);

        Map<Long, List<Payable>> openByProvider = new LinkedHashMap<>();
        List<Payable> standalone = new ArrayList<>();
        for (Payable payable : allPayables) {
            if (payable.getPayeeType() == PayeeType.PROVIDER && payable.getProviderId() != null && isOpen(payable)) {
                openByProvider.computeIfAbsent(payable.getProviderId(), k -> new ArrayList<>()).add(payable);
            } else {
                standalone.add(payable);
            }
        }

        List<Payable> grouped = new ArrayList<>();
        for (List<Payable> openLines : openByProvider.values()) {
            grouped.add(buildOpenPayableSummary(openLines));
        }
        grouped.addAll(standalone);

        if (status != null) {
            grouped = grouped.stream().filter(p -> p.getStatus() == status).toList();
        }

        return grouped.stream()
                .sorted(Comparator.comparing(Payable::getCreatedAt).reversed())
                .toList();
    }

    private boolean isOpen(Payable payable) {
        return payable.getStatus() == PayableStatus.PENDING || payable.getStatus() == PayableStatus.PARTIAL;
    }

    private Payable buildOpenPayableSummary(List<Payable> openLines) {
        Payable oldest = openLines.stream().min(Comparator.comparing(Payable::getCreatedAt)).orElseThrow();

        BigDecimal totalAmount = openLines.stream().map(Payable::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paidAmount = openLines.stream().map(Payable::getPaidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        PayableStatus status = paidAmount.compareTo(BigDecimal.ZERO) > 0 ? PayableStatus.PARTIAL : PayableStatus.PENDING;

        LocalDate dueDate = openLines.stream()
                .map(Payable::getDueDate)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);

        Payable summary = new Payable();
        summary.setId(oldest.getId());
        summary.setBusinessId(oldest.getBusinessId());
        summary.setPayeeType(oldest.getPayeeType());
        summary.setProviderId(oldest.getProviderId());
        summary.setPayeeName(oldest.getPayeeName());
        summary.setSource(oldest.getSource());
        summary.setSourceId(oldest.getSourceId());
        summary.setCreatedAt(oldest.getCreatedAt());
        summary.setTotalAmount(totalAmount);
        summary.setPaidAmount(paidAmount);
        summary.setStatus(status);
        summary.setDueDate(dueDate);
        return summary;
    }

    @Override
    public List<PayablePaymentResponse> payProviderPayable(Long businessId, Long providerId, PayablePaymentRequest request) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        providerPersistencePort.findByIdAndBusinessId(providerId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.PROVIDER_NOT_FOUND));

        List<Payable> openPayables = payablePersistencePort.findAllByFilters(businessId, null, PayeeType.PROVIDER, providerId).stream()
                .filter(p -> p.getStatus() == PayableStatus.PENDING || p.getStatus() == PayableStatus.PARTIAL)
                .sorted(Comparator.comparing(Payable::getCreatedAt))
                .toList();

        if (openPayables.isEmpty()) {
            throw new NotFoundException(DomainConstants.PROVIDER_HAS_NO_PAYABLE);
        }

        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(DomainConstants.PAYMENT_AMOUNT_INVALID);
        }

        BigDecimal totalPending = openPayables.stream()
                .map(p -> p.getTotalAmount().subtract(p.getPaidAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (request.amount().compareTo(totalPending) > 0) {
            throw new BadRequestException(DomainConstants.PAYABLE_AMOUNT_EXCEEDS_TOTAL);
        }

        BigDecimal remaining = request.amount();
        List<PayablePaymentResponse> responses = new ArrayList<>();

        for (Payable payable : openPayables) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal pending = payable.getTotalAmount().subtract(payable.getPaidAmount());
            if (pending.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal applied = remaining.min(pending);

            PayablePayment payment = new PayablePayment();
            payment.setPayableId(payable.getId());
            payment.setAmount(applied);
            payment.setPaymentMethod(request.paymentMethod());
            payment.setCreatedAt(LocalDateTime.now());
            PayablePayment savedPayment = payablePaymentPersistencePort.save(payment);

            payable.setPaidAmount(payable.getPaidAmount().add(applied));
            payable.setStatus(payable.getPaidAmount().compareTo(payable.getTotalAmount()) >= 0 ? PayableStatus.PAID : PayableStatus.PARTIAL);
            payablePersistencePort.save(payable);

            AuditLog auditLog = new AuditLog();
            auditLog.setBusinessId(businessId);
            auditLog.setUserId(userId);
            auditLog.setAction(DomainConstants.PAYABLE_PAYMENT_REGISTERED);
            auditLog.setEntity(DomainConstants.PAYABLE_ENTITY);
            auditLog.setEntityId(payable.getId());
            auditLog.setDetails(DomainConstants.PAYABLE_PAYMENT_REGISTERED_DETAILS + savedPayment.getId());
            auditLog.setCreatedAt(LocalDateTime.now());
            auditLogsPersistencePort.save(auditLog);

            responses.add(new PayablePaymentResponse(savedPayment, payable.getStatus()));
            remaining = remaining.subtract(applied);
        }

        return responses;
    }

    @Override
    public List<PayablePayment> findProviderPayablePayments(Long businessId, Long providerId) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        providerPersistencePort.findByIdAndBusinessId(providerId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.PROVIDER_NOT_FOUND));

        List<Payable> payables = payablePersistencePort.findAllByFilters(businessId, null, PayeeType.PROVIDER, providerId);

        return payables.stream()
                .flatMap(p -> payablePaymentPersistencePort.findAllByPayableId(p.getId()).stream())
                .sorted(Comparator.comparing(PayablePayment::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public PayablePaymentResponse updateProviderPayablePayment(Long businessId, Long providerId, Long paymentId, PayablePaymentRequest updateRequest) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        PayablePayment payment = payablePaymentPersistencePort.findById(paymentId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.PAYABLE_PAYMENT_NOT_FOUND));

        Payable payable = payablePersistencePort.findById(payment.getPayableId())
                .orElseThrow(() -> new NotFoundException(DomainConstants.PAYABLE_NOT_FOUND));

        if (!payable.getBusinessId().equals(businessId) || payable.getProviderId() == null || !payable.getProviderId().equals(providerId)) {
            throw new NotFoundException(DomainConstants.PAYABLE_PAYMENT_NOT_FOUND);
        }

        return applyPaymentEdit(businessId, userId, payable, paymentId, updateRequest);
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

        return applyPaymentEdit(businessId, userId, payable, paymentId, updateRequest);
    }

    private PayablePaymentResponse applyPaymentEdit(Long businessId, Long userId, Payable payable, Long paymentId, PayablePaymentRequest updateRequest) {
        if (payable.getStatus() == PayableStatus.CANCELLED) {
            throw new ConflictException(DomainConstants.PAYABLE_CANCELLED);
        }

        if (updateRequest.amount() == null || updateRequest.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(DomainConstants.PAYMENT_AMOUNT_INVALID);
        }

        List<PayablePayment> payments = payablePaymentPersistencePort.findAllByPayableId(payable.getId());
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
        auditLog.setEntityId(payable.getId());
        auditLog.setDetails(DomainConstants.PAYABLE_PAYMENT_UPDATED_DETAILS + savedPayment.getId());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogsPersistencePort.save(auditLog);

        return new PayablePaymentResponse(savedPayment, payable.getStatus());
    }
}
