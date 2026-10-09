package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IDebtServicePort;
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
public class DebtService implements IDebtServicePort {

    private final IDebtPersistencePort debtPersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final IDebtPaymentPersistencePort debtPaymentPersistencePort;
    private final IAuditLogsPersistencePort auditLogsPersistencePort;
    private final IClientPersistencePort clientPersistencePort;

    @Override
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

        if (debtCreateRequest.amount() == null || debtCreateRequest.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(DomainConstants.PAYMENT_AMOUNT_INVALID);
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
    public List<Debt> findGroupedDebts(Long businessId, DebtStatus status, Long clientId) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        List<Debt> allDebts = debtPersistencePort.findAllByFilters(businessId, null, clientId);

        Map<Long, List<Debt>> openByClient = new LinkedHashMap<>();
        List<Debt> standalone = new ArrayList<>();
        for (Debt debt : allDebts) {
            if (debt.getClientId() != null && isOpen(debt)) {
                openByClient.computeIfAbsent(debt.getClientId(), k -> new ArrayList<>()).add(debt);
            } else {
                standalone.add(debt);
            }
        }

        List<Debt> grouped = new ArrayList<>();
        for (List<Debt> openLines : openByClient.values()) {
            grouped.add(buildOpenDebtSummary(openLines));
        }
        grouped.addAll(standalone);

        if (status != null) {
            grouped = grouped.stream().filter(d -> d.getStatus() == status).toList();
        }

        return grouped.stream()
                .sorted(Comparator.comparing(Debt::getCreatedAt).reversed())
                .toList();
    }

    private boolean isOpen(Debt debt) {
        return debt.getStatus() == DebtStatus.PENDING || debt.getStatus() == DebtStatus.PARTIAL;
    }

    private Debt buildOpenDebtSummary(List<Debt> openLines) {
        Debt oldest = openLines.stream().min(Comparator.comparing(Debt::getCreatedAt)).orElseThrow();

        BigDecimal totalAmount = openLines.stream().map(Debt::getTotalAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal paidAmount = openLines.stream().map(Debt::getPaidAmount).reduce(BigDecimal.ZERO, BigDecimal::add);

        DebtStatus status = paidAmount.compareTo(BigDecimal.ZERO) > 0 ? DebtStatus.PARTIAL : DebtStatus.PENDING;

        LocalDate dueDate = openLines.stream()
                .map(Debt::getDueDate)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);

        Debt summary = new Debt();
        summary.setId(oldest.getId());
        summary.setBusinessId(oldest.getBusinessId());
        summary.setClientId(oldest.getClientId());
        summary.setDebtorName(oldest.getDebtorName());
        summary.setCreatedAt(oldest.getCreatedAt());
        summary.setTotalAmount(totalAmount);
        summary.setPaidAmount(paidAmount);
        summary.setStatus(status);
        summary.setDueDate(dueDate);
        return summary;
    }

    @Override
    public List<DebtPaymentResponse> payClientDebt(Long businessId, Long clientId, DebtCreateRequest request) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        clientPersistencePort.findByIdAndBusinessId(clientId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.CLIENT_NOT_FOUND));

        List<Debt> openDebts = debtPersistencePort.findAllByFilters(businessId, null, clientId).stream()
                .filter(d -> d.getStatus() == DebtStatus.PENDING || d.getStatus() == DebtStatus.PARTIAL)
                .sorted(Comparator.comparing(Debt::getCreatedAt))
                .toList();

        if (openDebts.isEmpty()) {
            throw new NotFoundException(DomainConstants.CLIENT_HAS_NO_DEBT);
        }

        if (request.amount() == null || request.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(DomainConstants.PAYMENT_AMOUNT_INVALID);
        }

        BigDecimal totalPending = openDebts.stream()
                .map(d -> d.getTotalAmount().subtract(d.getPaidAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (request.amount().compareTo(totalPending) > 0) {
            throw new BadRequestException(DomainConstants.DEBT_AMOUNT_EXCEEDS_TOTAL);
        }

        BigDecimal remaining = request.amount();
        List<DebtPaymentResponse> responses = new ArrayList<>();

        for (Debt debt : openDebts) {
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) break;

            BigDecimal pending = debt.getTotalAmount().subtract(debt.getPaidAmount());
            if (pending.compareTo(BigDecimal.ZERO) <= 0) continue;

            BigDecimal applied = remaining.min(pending);

            DebtPayment payment = new DebtPayment();
            payment.setDebtId(debt.getId());
            payment.setAmount(applied);
            payment.setPaymentMethod(request.paymentMethod());
            payment.setCreatedAt(LocalDateTime.now());
            DebtPayment savedPayment = debtPaymentPersistencePort.save(payment);

            debt.setPaidAmount(debt.getPaidAmount().add(applied));
            debt.setStatus(debt.getPaidAmount().compareTo(debt.getTotalAmount()) >= 0 ? DebtStatus.PAID : DebtStatus.PARTIAL);
            debtPersistencePort.save(debt);

            AuditLog auditLog = new AuditLog();
            auditLog.setBusinessId(businessId);
            auditLog.setUserId(userId);
            auditLog.setAction(DomainConstants.DEBT_PAYMENT_REGISTERED);
            auditLog.setEntity(DomainConstants.DEBT_ENTITY);
            auditLog.setEntityId(debt.getId());
            auditLog.setDetails(DomainConstants.DEBT_PAYMENT_REGISTERED_DETAILS + savedPayment.getId());
            auditLog.setCreatedAt(LocalDateTime.now());
            auditLogsPersistencePort.save(auditLog);

            responses.add(new DebtPaymentResponse(savedPayment, debt.getStatus()));
            remaining = remaining.subtract(applied);
        }

        return responses;
    }

    @Override
    public List<DebtPayment> findClientDebtPayments(Long businessId, Long clientId) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        clientPersistencePort.findByIdAndBusinessId(clientId, businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.CLIENT_NOT_FOUND));

        List<Debt> debts = debtPersistencePort.findAllByFilters(businessId, null, clientId);

        return debts.stream()
                .flatMap(d -> debtPaymentPersistencePort.findAllByDebtId(d.getId()).stream())
                .sorted(Comparator.comparing(DebtPayment::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public DebtPaymentResponse updateClientDebtPayment(Long businessId, Long clientId, Long paymentId, DebtCreateRequest updateRequest) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));
        if (!business.getOwnerId().equals(userId)) {
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        DebtPayment payment = debtPaymentPersistencePort.findById(paymentId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.DEBT_PAYMENT_NOT_FOUND));

        Debt debt = debtPersistencePort.findById(payment.getDebtId())
                .orElseThrow(() -> new NotFoundException(DomainConstants.DEBT_NOT_FOUND));

        if (!debt.getBusinessId().equals(businessId) || debt.getClientId() == null || !debt.getClientId().equals(clientId)) {
            throw new NotFoundException(DomainConstants.DEBT_PAYMENT_NOT_FOUND);
        }

        return applyPaymentEdit(businessId, userId, debt, paymentId, updateRequest);
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

    @Override
    public List<DebtPayment> findPaymentsByDebt(Long businessId, Long debtId) {
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

        return debtPaymentPersistencePort.findAllByDebtId(debtId);
    }

    @Override
    public DebtPaymentResponse updateDebtPayment(
            Long businessId,
            Long debtId,
            Long paymentId,
            DebtCreateRequest updateRequest
    ) {
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

        return applyPaymentEdit(businessId, userId, debt, paymentId, updateRequest);
    }

    private DebtPaymentResponse applyPaymentEdit(Long businessId, Long userId, Debt debt, Long paymentId, DebtCreateRequest updateRequest) {
        if (debt.getStatus() == DebtStatus.CANCELLED) {
            throw new ConflictException(DomainConstants.DEBT_CANCELLED);
        }

        if (updateRequest.amount() == null || updateRequest.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(DomainConstants.PAYMENT_AMOUNT_INVALID);
        }

        List<DebtPayment> payments = debtPaymentPersistencePort.findAllByDebtId(debt.getId());
        DebtPayment target = payments.stream()
                .filter(p -> p.getId().equals(paymentId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException(DomainConstants.DEBT_PAYMENT_NOT_FOUND));

        BigDecimal newPaidAmount = payments.stream()
                .map(p -> p.getId().equals(paymentId) ? updateRequest.amount() : p.getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (newPaidAmount.compareTo(debt.getTotalAmount()) > 0) {
            throw new BadRequestException(DomainConstants.DEBT_AMOUNT_EXCEEDS_TOTAL);
        }

        target.setAmount(updateRequest.amount());
        target.setPaymentMethod(updateRequest.paymentMethod());
        DebtPayment savedPayment = debtPaymentPersistencePort.save(target);

        debt.setPaidAmount(newPaidAmount);
        if (newPaidAmount.compareTo(BigDecimal.ZERO) == 0) {
            debt.setStatus(DebtStatus.PENDING);
        } else if (newPaidAmount.compareTo(debt.getTotalAmount()) >= 0) {
            debt.setStatus(DebtStatus.PAID);
        } else {
            debt.setStatus(DebtStatus.PARTIAL);
        }
        debtPersistencePort.save(debt);

        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessId(businessId);
        auditLog.setUserId(userId);
        auditLog.setAction(DomainConstants.DEBT_PAYMENT_UPDATED);
        auditLog.setEntity(DomainConstants.DEBT_ENTITY);
        auditLog.setEntityId(debt.getId());
        auditLog.setDetails(DomainConstants.DEBT_PAYMENT_UPDATED_DETAILS + savedPayment.getId());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogsPersistencePort.save(auditLog);

        return new DebtPaymentResponse(savedPayment, debt.getStatus());
    }

}
