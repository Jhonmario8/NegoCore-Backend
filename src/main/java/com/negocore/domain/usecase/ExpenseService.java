package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IExpenseServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.*;
import com.negocore.domain.spi.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
public class ExpenseService implements IExpenseServicePort {

    private final IExpensePersistencePort expensePersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final IProviderPersistencePort providerPersistencePort;
    private final IPayablePersistencePort payablePersistencePort;
    private final IAuditLogsPersistencePort auditLogsPersistencePort;

    @Override
    @Transactional
    public Expense registerExpense(Long businessId, ExpenseCreateRequest expenseCreateRequest) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)){
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        boolean paid = expenseCreateRequest.paid() == null || expenseCreateRequest.paid();

        Provider provider = null;
        if (!paid) {
            if (expenseCreateRequest.payeeType() == null) {
                throw new BadRequestException(DomainConstants.PAYEE_TYPE_REQUIRED);
            }
            if (expenseCreateRequest.payeeType() == PayeeType.PROVIDER) {
                if (expenseCreateRequest.providerId() == null) {
                    throw new BadRequestException(DomainConstants.PROVIDER_ID_REQUIRED_FOR_PROVIDER_PAYEE);
                }
                provider = providerPersistencePort.findByIdAndBusinessId(expenseCreateRequest.providerId(), businessId)
                        .orElseThrow(() -> new NotFoundException(DomainConstants.PROVIDER_NOT_FOUND));
            } else {
                if (expenseCreateRequest.payeeName() == null || expenseCreateRequest.payeeName().isBlank()) {
                    throw new BadRequestException(DomainConstants.PAYEE_NAME_REQUIRED_FOR_OTHER_PAYEE);
                }
            }
        }

        Expense expense = new Expense();
        expense.setBusinessId(businessId);
        expense.setCategory(expenseCreateRequest.category());
        expense.setDescription(expenseCreateRequest.description());
        expense.setAmount(expenseCreateRequest.amount());
        expense.setPaid(paid);
        expense.setCreatedAt(LocalDateTime.now());
        Expense savedExpense = expensePersistencePort.save(expense);

        if (!paid) {
            Payable payable = new Payable();
            payable.setBusinessId(businessId);
            payable.setPayeeType(expenseCreateRequest.payeeType());
            payable.setProviderId(expenseCreateRequest.payeeType() == PayeeType.PROVIDER ? provider.getId() : null);
            payable.setPayeeName(expenseCreateRequest.payeeType() == PayeeType.OTHER ? expenseCreateRequest.payeeName() : null);
            payable.setSource(PayableSource.EXPENSE);
            payable.setSourceId(savedExpense.getId());
            payable.setTotalAmount(expenseCreateRequest.amount());
            payable.setPaidAmount(BigDecimal.ZERO);
            payable.setStatus(PayableStatus.PENDING);
            payable.setDueDate(expenseCreateRequest.dueDate());
            payable.setCreatedAt(LocalDateTime.now());
            payablePersistencePort.save(payable);
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessId(businessId);
        auditLog.setUserId(userId);
        auditLog.setAction(DomainConstants.EXPENSE_CREATED);
        auditLog.setEntity(DomainConstants.EXPENSE_ENTITY);
        auditLog.setEntityId(savedExpense.getId());
        auditLog.setDetails(DomainConstants.EXPENSE_CREATED_DETAILS + savedExpense.getId());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogsPersistencePort.save(auditLog);

        return savedExpense;
    }

    @Override
    public List<Expense> findExpenses(
            Long businessId,
            LocalDateTime from,
            LocalDateTime to
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

        if (from != null && to != null && from.isAfter(to)) {
            throw new BadRequestException(
                    DomainConstants.INVALID_DATE_RANGE
            );
        }

        return expensePersistencePort.findAllByFilters(
                businessId,
                from,
                to
        );
    }

}
