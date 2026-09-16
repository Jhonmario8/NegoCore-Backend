package com.negocore.infrastructure.config;

import com.negocore.domain.api.*;
import com.negocore.domain.spi.*;
import com.negocore.domain.usecase.*;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@AllArgsConstructor
public class BeanConfiguration {

    private final IUserPersistencePort userPersistencePort;
    private final ITokenServicePort tokenServicePort;
    private final IAuthenticationServicePort authenticationServicePort;
    private final IBusinessPersistencePort businessPersistencePort;
    private final ICategoryPersistencePort categoryPersistencePort;
    private final IProductPersistencePort productPersistencePort;
    private final ISaleItemsPersistencePort saleItemsPersistencePort;
    private final IDebtPersistencePort debtPersistencePort;
    private final ISalePersistencePort salePersistencePort;
    private final IDebtPaymentPersistencePort debtPaymentPersistencePort;
    private final IExpensePersistencePort expensePersistencePort;
    private final IClientPersistencePort clientPersistencePort;
    private final IAuditLogsPersistencePort auditLogsPersistencePort;
    private final IProviderPersistencePort providerPersistencePort;
    private final IPurchasePersistencePort purchasePersistencePort;
    private final IPurchaseItemsPersistencePort purchaseItemsPersistencePort;
    private final IPayablePersistencePort payablePersistencePort;
    private final IPayablePaymentPersistencePort payablePaymentPersistencePort;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public IUserServicePort userServicePort(IPasswordServicePort passwordServicePort){
        return new UserService(userPersistencePort, passwordServicePort, tokenServicePort );
    }

    @Bean
    public IBusinessServicePort businessServicePort(){
        return new BusinessService(businessPersistencePort, authenticationServicePort);
    }

    @Bean
    public ICategoryServicePort categoryServicePort(){
        return new CategoryService(categoryPersistencePort, businessPersistencePort, authenticationServicePort);
    }
    @Bean
    public IProductServicePort productServicePort(){
        return new ProductService(productPersistencePort, businessPersistencePort, authenticationServicePort, categoryPersistencePort);
    }


    @Bean
    public ISaleServicePort saleServicePort(){
        return new SaleService(salePersistencePort, authenticationServicePort, businessPersistencePort, productPersistencePort, saleItemsPersistencePort, debtPersistencePort, debtPaymentPersistencePort, auditLogsPersistencePort);
    }

    @Bean
    public IExpenseServicePort expenseServicePort(){
        return new ExpenseService(expensePersistencePort, authenticationServicePort, businessPersistencePort, providerPersistencePort, payablePersistencePort, auditLogsPersistencePort);
    }

    @Bean
    public IClientServicePort clientServicePort() {
        return new ClientService(clientPersistencePort, businessPersistencePort, authenticationServicePort);
    }

    @Bean
    public IDebtServicePort debtServicePort() {
        return new DebtService(debtPersistencePort, authenticationServicePort, businessPersistencePort, debtPaymentPersistencePort, auditLogsPersistencePort);
    }

    @Bean
    public IBalanceReportServicePort balanceReportServicePort() {
        return new BalanceReportService(authenticationServicePort, businessPersistencePort, salePersistencePort, expensePersistencePort);
    }

    @Bean IAuditLogServicePort auditLogServicePort() {
        return new AuditLogService(authenticationServicePort, businessPersistencePort, auditLogsPersistencePort);
    }

    @Bean IProviderServicePort providerServicePort() {
        return new ProviderService(providerPersistencePort, businessPersistencePort, authenticationServicePort);
    }

    @Bean
    public IPurchaseServicePort purchaseServicePort() {
        return new PurchaseService(
                purchasePersistencePort,
                purchaseItemsPersistencePort,
                authenticationServicePort,
                businessPersistencePort,
                providerPersistencePort,
                productPersistencePort,
                payablePersistencePort,
                auditLogsPersistencePort
        );
    }

    @Bean
    public IQuoteServicePort quoteServicePort() {
        return new QuoteService(
                authenticationServicePort,
                businessPersistencePort,
                productPersistencePort,
                userPersistencePort
        );
    }

    @Bean
    public IPayableServicePort payableServicePort() {
        return new PayableService(
                payablePersistencePort,
                payablePaymentPersistencePort,
                authenticationServicePort,
                businessPersistencePort,
                auditLogsPersistencePort
        );
    }
}
