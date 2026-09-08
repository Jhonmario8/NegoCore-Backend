package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IExpenseServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.*;
import com.negocore.domain.spi.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
public class ExpenseService implements IExpenseServicePort {

    private final IExpensePersistencePort expensePersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;
    private final ICashRegisterPersistencePort cashRegisterPersistencePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final ICashMovementPersistencePort cashMovementPersistencePort;
    private final IAuditLogsPersistencePort auditLogsPersistencePort;

    @Override
    @Transactional
    public Expense registerExpense(Long businessId, Expense expense) {
        Long userId = authenticationServicePort.getCurrentUserId();
        Business business = businessPersistencePort.findById(businessId)
                .orElseThrow(() -> new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND));

        if (!business.getOwnerId().equals(userId)){
            throw new NotFoundException(DomainConstants.BUSINESS_NOT_FOUND);
        }

        Optional<CashRegister> cashRegister = cashRegisterPersistencePort
                .findOpenCashRegisterByBusinessIdAndStatus(businessId, CashRegisterStatus.OPEN);

        cashRegister.ifPresent(openCashRegister -> expense.setCashRegisterId(openCashRegister.getId()));
        expense.setBusinessId(businessId);
        expense.setCreatedAt(LocalDateTime.now());
        Expense saveExpense = expensePersistencePort.save(expense);
        if (cashRegister.isPresent()) {
             CashRegister openCashRegister = cashRegister.get();

            CashMovement cashMovement = new CashMovement();
            cashMovement.setCashRegisterId(openCashRegister.getId());
            cashMovement.setType(CashMovementType.EXPENSE);
            cashMovement.setAmount(saveExpense.getAmount());
            cashMovement.setDescription(saveExpense.getDescription());
            cashMovement.setCreatedAt(LocalDateTime.now());
            cashMovement.setReferenceId(saveExpense.getId());
            cashMovementPersistencePort.save(cashMovement);
        }

        AuditLog auditLog = new AuditLog();
        auditLog.setBusinessId(businessId);
        auditLog.setUserId(userId);
        auditLog.setAction(DomainConstants.EXPENSE_CREATED);
        auditLog.setEntity(DomainConstants.EXPENSE_ENTITY);
        auditLog.setEntityId(saveExpense.getId());
        auditLog.setDetails(DomainConstants.EXPENSE_CREATED_DETAILS + saveExpense.getId());
        auditLog.setCreatedAt(LocalDateTime.now());
        auditLogsPersistencePort.save(auditLog);

        return saveExpense;
    }
}
