package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.ICashRegisterServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.Business;
import com.negocore.domain.model.CashRegister;
import com.negocore.domain.model.CashRegisterStatus;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.domain.spi.ICashRegisterPersistencePort;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CashRegisterService implements ICashRegisterServicePort {

    private final ICashRegisterPersistencePort cashRegisterPersistencePort;
    private final IAuthenticationServicePort authenticationServicePort;
    private final IBusinessPersistencePort businessPersistencePort;

    @Override
    public CashRegister openCashRegister(Long businessId, Double openingAmount) {
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
}
