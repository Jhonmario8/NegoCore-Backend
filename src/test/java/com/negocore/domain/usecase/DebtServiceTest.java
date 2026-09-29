package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.ConflictException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.Business;
import com.negocore.domain.model.Client;
import com.negocore.domain.model.Debt;
import com.negocore.domain.model.DebtCreateRequest;
import com.negocore.domain.model.DebtPayment;
import com.negocore.domain.model.DebtPaymentMethod;
import com.negocore.domain.model.DebtPaymentResponse;
import com.negocore.domain.model.DebtStatus;
import com.negocore.domain.spi.IAuditLogsPersistencePort;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.domain.spi.IClientPersistencePort;
import com.negocore.domain.spi.IDebtPaymentPersistencePort;
import com.negocore.domain.spi.IDebtPersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DebtServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long BUSINESS_ID = 10L;
    private static final Long DEBT_ID = 200L;

    @Mock private IDebtPersistencePort debtPersistencePort;
    @Mock private IAuthenticationServicePort authenticationServicePort;
    @Mock private IBusinessPersistencePort businessPersistencePort;
    @Mock private IDebtPaymentPersistencePort debtPaymentPersistencePort;
    @Mock private IAuditLogsPersistencePort auditLogsPersistencePort;
    @Mock private IClientPersistencePort clientPersistencePort;

    @InjectMocks private DebtService debtService;

    @BeforeEach
    void setUp() {
        when(authenticationServicePort.getCurrentUserId()).thenReturn(USER_ID);
    }

    private Business ownedBusiness() {
        Business business = new Business();
        business.setId(BUSINESS_ID);
        business.setOwnerId(USER_ID);
        return business;
    }

    private Debt aDebt(DebtStatus status, BigDecimal total, BigDecimal paid) {
        Debt debt = new Debt();
        debt.setId(DEBT_ID);
        debt.setBusinessId(BUSINESS_ID);
        debt.setStatus(status);
        debt.setTotalAmount(total);
        debt.setPaidAmount(paid);
        return debt;
    }

    @Test
    @DisplayName("Un abono parcial deja la deuda en PARTIAL")
    void createDebt_partialPayment_leavesStatusPartial() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Debt debt = aDebt(DebtStatus.PENDING, BigDecimal.valueOf(1000), BigDecimal.ZERO);
        when(debtPersistencePort.findById(DEBT_ID)).thenReturn(Optional.of(debt));
        when(debtPaymentPersistencePort.save(any(DebtPayment.class))).thenAnswer(inv -> inv.getArgument(0));

        DebtPaymentResponse response = debtService.createDebt(
                BUSINESS_ID, DEBT_ID, new DebtCreateRequest(BigDecimal.valueOf(400), DebtPaymentMethod.CASH)
        );

        assertThat(response.getStatus()).isEqualTo(DebtStatus.PARTIAL);
        assertThat(debt.getPaidAmount()).isEqualByComparingTo(BigDecimal.valueOf(400));
    }

    @Test
    @DisplayName("Un abono que completa el total deja la deuda en PAID")
    void createDebt_paymentCompletesTotal_leavesStatusPaid() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Debt debt = aDebt(DebtStatus.PARTIAL, BigDecimal.valueOf(1000), BigDecimal.valueOf(400));
        when(debtPersistencePort.findById(DEBT_ID)).thenReturn(Optional.of(debt));
        when(debtPaymentPersistencePort.save(any(DebtPayment.class))).thenAnswer(inv -> inv.getArgument(0));

        DebtPaymentResponse response = debtService.createDebt(
                BUSINESS_ID, DEBT_ID, new DebtCreateRequest(BigDecimal.valueOf(600), DebtPaymentMethod.CASH)
        );

        assertThat(response.getStatus()).isEqualTo(DebtStatus.PAID);
    }

    @Test
    @DisplayName("Abonar a una deuda ya PAID lanza ConflictException")
    void createDebt_alreadyPaid_throwsConflict() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Debt debt = aDebt(DebtStatus.PAID, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));
        when(debtPersistencePort.findById(DEBT_ID)).thenReturn(Optional.of(debt));

        assertThatThrownBy(() -> debtService.createDebt(
                BUSINESS_ID, DEBT_ID, new DebtCreateRequest(BigDecimal.TEN, DebtPaymentMethod.CASH)
        ))
                .isInstanceOf(ConflictException.class)
                .hasMessage(DomainConstants.DEBT_ALREADY_PAID);

        verify(debtPaymentPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Abonar a una deuda CANCELLED lanza ConflictException")
    void createDebt_cancelled_throwsConflict() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Debt debt = aDebt(DebtStatus.CANCELLED, BigDecimal.valueOf(1000), BigDecimal.ZERO);
        when(debtPersistencePort.findById(DEBT_ID)).thenReturn(Optional.of(debt));

        assertThatThrownBy(() -> debtService.createDebt(
                BUSINESS_ID, DEBT_ID, new DebtCreateRequest(BigDecimal.TEN, DebtPaymentMethod.CASH)
        ))
                .isInstanceOf(ConflictException.class)
                .hasMessage(DomainConstants.DEBT_CANCELLED);
    }

    @Test
    @DisplayName("Un abono mayor al saldo pendiente lanza BadRequestException")
    void createDebt_amountExceedsPending_throwsBadRequest() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Debt debt = aDebt(DebtStatus.PARTIAL, BigDecimal.valueOf(1000), BigDecimal.valueOf(800));
        when(debtPersistencePort.findById(DEBT_ID)).thenReturn(Optional.of(debt));

        assertThatThrownBy(() -> debtService.createDebt(
                BUSINESS_ID, DEBT_ID, new DebtCreateRequest(BigDecimal.valueOf(500), DebtPaymentMethod.CASH)
        ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.DEBT_AMOUNT_EXCEEDS_TOTAL);

        verify(debtPaymentPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("registerLoan con clientId limpia el debtorName")
    void registerLoan_withClient_clearsDebtorName() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        when(clientPersistencePort.findByIdAndBusinessId(55L, BUSINESS_ID))
                .thenReturn(Optional.of(new Client()));
        when(debtPersistencePort.save(any(Debt.class))).thenAnswer(inv -> inv.getArgument(0));

        Debt loan = new Debt();
        loan.setClientId(55L);
        loan.setDebtorName("nombre que debe ser ignorado");

        Debt saved = debtService.registerLoan(BUSINESS_ID, loan);

        assertThat(saved.getDebtorName()).isNull();
        assertThat(saved.getStatus()).isEqualTo(DebtStatus.PENDING);
        assertThat(saved.getDueDate()).isEqualTo(LocalDate.now().plusDays(30));
    }

    @Test
    @DisplayName("registerLoan sin cliente y sin debtorName lanza BadRequestException")
    void registerLoan_withoutClientOrDebtorName_throwsBadRequest() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));

        Debt loan = new Debt();

        assertThatThrownBy(() -> debtService.registerLoan(BUSINESS_ID, loan))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.DEBTOR_NAME_REQUIRED_FOR_NO_CLIENT);

        verify(debtPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Un usuario que no es dueño del negocio no puede crear préstamos en él")
    void registerLoan_nonOwner_throwsNotFound() {
        Business business = ownedBusiness();
        business.setOwnerId(999L);
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(business));

        Debt loan = new Debt();
        loan.setDebtorName("Juan");

        assertThatThrownBy(() -> debtService.registerLoan(BUSINESS_ID, loan))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(DomainConstants.BUSINESS_NOT_FOUND);
    }
}
