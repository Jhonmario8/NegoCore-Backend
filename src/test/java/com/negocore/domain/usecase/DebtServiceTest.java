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
import java.time.LocalDateTime;
import java.util.List;
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
    @DisplayName("Un abono con monto cero lanza BadRequestException")
    void createDebt_zeroAmount_throwsBadRequest() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Debt debt = aDebt(DebtStatus.PENDING, BigDecimal.valueOf(1000), BigDecimal.ZERO);
        when(debtPersistencePort.findById(DEBT_ID)).thenReturn(Optional.of(debt));

        assertThatThrownBy(() -> debtService.createDebt(
                BUSINESS_ID, DEBT_ID, new DebtCreateRequest(BigDecimal.ZERO, DebtPaymentMethod.CASH)
        ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.PAYMENT_AMOUNT_INVALID);

        verify(debtPaymentPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Un abono con monto negativo lanza BadRequestException")
    void createDebt_negativeAmount_throwsBadRequest() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Debt debt = aDebt(DebtStatus.PENDING, BigDecimal.valueOf(1000), BigDecimal.ZERO);
        when(debtPersistencePort.findById(DEBT_ID)).thenReturn(Optional.of(debt));

        assertThatThrownBy(() -> debtService.createDebt(
                BUSINESS_ID, DEBT_ID, new DebtCreateRequest(BigDecimal.valueOf(-50), DebtPaymentMethod.CASH)
        ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.PAYMENT_AMOUNT_INVALID);

        verify(debtPaymentPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Un abono con monto nulo lanza BadRequestException")
    void createDebt_nullAmount_throwsBadRequest() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Debt debt = aDebt(DebtStatus.PENDING, BigDecimal.valueOf(1000), BigDecimal.ZERO);
        when(debtPersistencePort.findById(DEBT_ID)).thenReturn(Optional.of(debt));

        assertThatThrownBy(() -> debtService.createDebt(
                BUSINESS_ID, DEBT_ID, new DebtCreateRequest(null, DebtPaymentMethod.CASH)
        ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.PAYMENT_AMOUNT_INVALID);

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

    private Debt aClientDebt(Long id, Long clientId, DebtStatus status, int total, int paid, LocalDateTime createdAt) {
        Debt debt = aDebt(status, BigDecimal.valueOf(total), BigDecimal.valueOf(paid));
        debt.setId(id);
        debt.setClientId(clientId);
        debt.setCreatedAt(createdAt);
        debt.setDueDate(createdAt.toLocalDate().plusDays(30));
        return debt;
    }

    @Test
    @DisplayName("Una deuda nueva de un cliente que ya pagó todo no se suma a la deuda vieja pagada")
    void findGroupedDebts_newDebtAfterFullyPaidOne_isNotMergedWithPaidDebt() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        LocalDateTime now = LocalDateTime.now();
        Debt oldPaid = aClientDebt(1L, 7L, DebtStatus.PAID, 100, 100, now.minusDays(20));
        Debt newDebt = aClientDebt(2L, 7L, DebtStatus.PENDING, 50, 0, now.minusDays(1));
        when(debtPersistencePort.findAllByFilters(BUSINESS_ID, null, null)).thenReturn(List.of(oldPaid, newDebt));

        List<Debt> result = debtService.findGroupedDebts(BUSINESS_ID, null, null);

        assertThat(result).hasSize(2);
        Debt open = result.stream().filter(d -> d.getStatus() == DebtStatus.PENDING).findFirst().orElseThrow();
        assertThat(open.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(open.getPaidAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(open.getCreatedAt()).isEqualTo(newDebt.getCreatedAt());
        Debt closed = result.stream().filter(d -> d.getStatus() == DebtStatus.PAID).findFirst().orElseThrow();
        assertThat(closed.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(closed.getPaidAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Las deudas abiertas del mismo cliente siguen sumándose en una sola fila")
    void findGroupedDebts_openDebtsOfSameClient_areMergedIntoOneRow() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        LocalDateTime now = LocalDateTime.now();
        Debt first = aClientDebt(1L, 7L, DebtStatus.PARTIAL, 100, 40, now.minusDays(10));
        Debt second = aClientDebt(2L, 7L, DebtStatus.PENDING, 50, 0, now.minusDays(2));
        when(debtPersistencePort.findAllByFilters(BUSINESS_ID, null, null)).thenReturn(List.of(first, second));

        List<Debt> result = debtService.findGroupedDebts(BUSINESS_ID, null, null);

        assertThat(result).hasSize(1);
        Debt summary = result.get(0);
        assertThat(summary.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(summary.getPaidAmount()).isEqualByComparingTo(BigDecimal.valueOf(40));
        assertThat(summary.getStatus()).isEqualTo(DebtStatus.PARTIAL);
        assertThat(summary.getCreatedAt()).isEqualTo(first.getCreatedAt());
        assertThat(summary.getDueDate()).isEqualTo(first.getDueDate());
    }

    @Test
    @DisplayName("Una deuda pagada de un cliente con otra deuda abierta se muestra aparte, sin entrar en el saldo abierto")
    void findGroupedDebts_paidDebtAlongsideOpenOne_staysSeparate() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        LocalDateTime now = LocalDateTime.now();
        Debt paid = aClientDebt(1L, 7L, DebtStatus.PAID, 100, 100, now.minusDays(30));
        Debt partial = aClientDebt(2L, 7L, DebtStatus.PARTIAL, 80, 30, now.minusDays(5));
        when(debtPersistencePort.findAllByFilters(BUSINESS_ID, null, 7L)).thenReturn(List.of(paid, partial));

        List<Debt> result = debtService.findGroupedDebts(BUSINESS_ID, null, 7L);

        assertThat(result).extracting(Debt::getStatus)
                .containsExactlyInAnyOrder(DebtStatus.PAID, DebtStatus.PARTIAL);
        Debt open = result.stream().filter(d -> d.getStatus() == DebtStatus.PARTIAL).findFirst().orElseThrow();
        assertThat(open.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(80));
        assertThat(open.getPaidAmount()).isEqualByComparingTo(BigDecimal.valueOf(30));
    }

    @Test
    @DisplayName("Una deuda cancelada no se suma al saldo abierto del cliente")
    void findGroupedDebts_cancelledDebt_isNotIncludedInOpenBalance() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        LocalDateTime now = LocalDateTime.now();
        Debt cancelled = aClientDebt(1L, 7L, DebtStatus.CANCELLED, 500, 0, now.minusDays(9));
        Debt open = aClientDebt(2L, 7L, DebtStatus.PENDING, 60, 0, now.minusDays(1));
        when(debtPersistencePort.findAllByFilters(BUSINESS_ID, null, null)).thenReturn(List.of(cancelled, open));

        List<Debt> result = debtService.findGroupedDebts(BUSINESS_ID, DebtStatus.PENDING, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(60));
    }

    @Test
    @DisplayName("Listar deudas agrupadas de un negocio ajeno lanza NotFoundException")
    void findGroupedDebts_nonOwner_throwsNotFound() {
        Business business = ownedBusiness();
        business.setOwnerId(999L);
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(business));

        assertThatThrownBy(() -> debtService.findGroupedDebts(BUSINESS_ID, null, null))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(DomainConstants.BUSINESS_NOT_FOUND);

        verify(debtPersistencePort, never()).findAllByFilters(any(), any(), any());
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
