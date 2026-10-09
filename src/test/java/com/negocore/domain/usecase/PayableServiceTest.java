package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.ConflictException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.Business;
import com.negocore.domain.model.Payable;
import com.negocore.domain.model.PayablePayment;
import com.negocore.domain.model.PayablePaymentMethod;
import com.negocore.domain.model.PayablePaymentRequest;
import com.negocore.domain.model.PayablePaymentResponse;
import com.negocore.domain.model.PayableStatus;
import com.negocore.domain.model.PayeeType;
import com.negocore.domain.spi.IAuditLogsPersistencePort;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.domain.spi.IPayablePaymentPersistencePort;
import com.negocore.domain.spi.IPayablePersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
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
class PayableServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long BUSINESS_ID = 10L;
    private static final Long PAYABLE_ID = 300L;

    @Mock private IPayablePersistencePort payablePersistencePort;
    @Mock private IPayablePaymentPersistencePort payablePaymentPersistencePort;
    @Mock private IAuthenticationServicePort authenticationServicePort;
    @Mock private IBusinessPersistencePort businessPersistencePort;
    @Mock private IAuditLogsPersistencePort auditLogsPersistencePort;

    @InjectMocks private PayableService payableService;

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

    private Payable aPayable(PayableStatus status, BigDecimal total, BigDecimal paid) {
        Payable payable = new Payable();
        payable.setId(PAYABLE_ID);
        payable.setBusinessId(BUSINESS_ID);
        payable.setStatus(status);
        payable.setTotalAmount(total);
        payable.setPaidAmount(paid);
        return payable;
    }

    @Test
    @DisplayName("Un abono parcial deja la cuenta por pagar en PARTIAL")
    void createPayablePayment_partialPayment_leavesStatusPartial() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Payable payable = aPayable(PayableStatus.PENDING, BigDecimal.valueOf(1000), BigDecimal.ZERO);
        when(payablePersistencePort.findById(PAYABLE_ID)).thenReturn(Optional.of(payable));
        when(payablePaymentPersistencePort.save(any(PayablePayment.class))).thenAnswer(inv -> inv.getArgument(0));

        PayablePaymentResponse response = payableService.createPayablePayment(
                BUSINESS_ID, PAYABLE_ID, new PayablePaymentRequest(BigDecimal.valueOf(400), PayablePaymentMethod.CASH)
        );

        assertThat(response.getStatus()).isEqualTo(PayableStatus.PARTIAL);
    }

    @Test
    @DisplayName("Un abono que completa el total deja la cuenta por pagar en PAID")
    void createPayablePayment_completesTotal_leavesStatusPaid() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Payable payable = aPayable(PayableStatus.PARTIAL, BigDecimal.valueOf(1000), BigDecimal.valueOf(400));
        when(payablePersistencePort.findById(PAYABLE_ID)).thenReturn(Optional.of(payable));
        when(payablePaymentPersistencePort.save(any(PayablePayment.class))).thenAnswer(inv -> inv.getArgument(0));

        PayablePaymentResponse response = payableService.createPayablePayment(
                BUSINESS_ID, PAYABLE_ID, new PayablePaymentRequest(BigDecimal.valueOf(600), PayablePaymentMethod.CASH)
        );

        assertThat(response.getStatus()).isEqualTo(PayableStatus.PAID);
    }

    @Test
    @DisplayName("Pagar una cuenta ya PAID lanza ConflictException")
    void createPayablePayment_alreadyPaid_throwsConflict() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Payable payable = aPayable(PayableStatus.PAID, BigDecimal.valueOf(1000), BigDecimal.valueOf(1000));
        when(payablePersistencePort.findById(PAYABLE_ID)).thenReturn(Optional.of(payable));

        assertThatThrownBy(() -> payableService.createPayablePayment(
                BUSINESS_ID, PAYABLE_ID, new PayablePaymentRequest(BigDecimal.TEN, PayablePaymentMethod.CASH)
        ))
                .isInstanceOf(ConflictException.class)
                .hasMessage(DomainConstants.PAYABLE_ALREADY_PAID);

        verify(payablePaymentPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Pagar una cuenta CANCELLED lanza ConflictException")
    void createPayablePayment_cancelled_throwsConflict() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Payable payable = aPayable(PayableStatus.CANCELLED, BigDecimal.valueOf(1000), BigDecimal.ZERO);
        when(payablePersistencePort.findById(PAYABLE_ID)).thenReturn(Optional.of(payable));

        assertThatThrownBy(() -> payableService.createPayablePayment(
                BUSINESS_ID, PAYABLE_ID, new PayablePaymentRequest(BigDecimal.TEN, PayablePaymentMethod.CASH)
        ))
                .isInstanceOf(ConflictException.class)
                .hasMessage(DomainConstants.PAYABLE_CANCELLED);
    }

    @Test
    @DisplayName("Un abono mayor al saldo pendiente lanza BadRequestException")
    void createPayablePayment_amountExceedsPending_throwsBadRequest() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Payable payable = aPayable(PayableStatus.PARTIAL, BigDecimal.valueOf(1000), BigDecimal.valueOf(800));
        when(payablePersistencePort.findById(PAYABLE_ID)).thenReturn(Optional.of(payable));

        assertThatThrownBy(() -> payableService.createPayablePayment(
                BUSINESS_ID, PAYABLE_ID, new PayablePaymentRequest(BigDecimal.valueOf(500), PayablePaymentMethod.CASH)
        ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.PAYABLE_AMOUNT_EXCEEDS_TOTAL);

        verify(payablePaymentPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Un abono con monto cero lanza BadRequestException")
    void createPayablePayment_zeroAmount_throwsBadRequest() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Payable payable = aPayable(PayableStatus.PENDING, BigDecimal.valueOf(1000), BigDecimal.ZERO);
        when(payablePersistencePort.findById(PAYABLE_ID)).thenReturn(Optional.of(payable));

        assertThatThrownBy(() -> payableService.createPayablePayment(
                BUSINESS_ID, PAYABLE_ID, new PayablePaymentRequest(BigDecimal.ZERO, PayablePaymentMethod.CASH)
        ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.PAYMENT_AMOUNT_INVALID);

        verify(payablePaymentPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Un abono con monto negativo lanza BadRequestException")
    void createPayablePayment_negativeAmount_throwsBadRequest() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Payable payable = aPayable(PayableStatus.PENDING, BigDecimal.valueOf(1000), BigDecimal.ZERO);
        when(payablePersistencePort.findById(PAYABLE_ID)).thenReturn(Optional.of(payable));

        assertThatThrownBy(() -> payableService.createPayablePayment(
                BUSINESS_ID, PAYABLE_ID, new PayablePaymentRequest(BigDecimal.valueOf(-50), PayablePaymentMethod.CASH)
        ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.PAYMENT_AMOUNT_INVALID);

        verify(payablePaymentPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Un abono con monto nulo lanza BadRequestException")
    void createPayablePayment_nullAmount_throwsBadRequest() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Payable payable = aPayable(PayableStatus.PENDING, BigDecimal.valueOf(1000), BigDecimal.ZERO);
        when(payablePersistencePort.findById(PAYABLE_ID)).thenReturn(Optional.of(payable));

        assertThatThrownBy(() -> payableService.createPayablePayment(
                BUSINESS_ID, PAYABLE_ID, new PayablePaymentRequest(null, PayablePaymentMethod.CASH)
        ))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.PAYMENT_AMOUNT_INVALID);

        verify(payablePaymentPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Un usuario que no es dueño del negocio no puede pagar sus cuentas por pagar")
    void createPayablePayment_nonOwner_throwsNotFound() {
        Business business = ownedBusiness();
        business.setOwnerId(999L);
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(business));

        assertThatThrownBy(() -> payableService.createPayablePayment(
                BUSINESS_ID, PAYABLE_ID, new PayablePaymentRequest(BigDecimal.TEN, PayablePaymentMethod.CASH)
        ))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(DomainConstants.BUSINESS_NOT_FOUND);

        verify(payablePersistencePort, never()).findById(any());
    }

    private Payable aProviderPayable(Long id, Long providerId, PayableStatus status, int total, int paid, LocalDateTime createdAt) {
        Payable payable = aPayable(status, BigDecimal.valueOf(total), BigDecimal.valueOf(paid));
        payable.setId(id);
        payable.setPayeeType(PayeeType.PROVIDER);
        payable.setProviderId(providerId);
        payable.setCreatedAt(createdAt);
        payable.setDueDate(createdAt.toLocalDate().plusDays(30));
        return payable;
    }

    @Test
    @DisplayName("Una cuenta por pagar nueva de un proveedor que ya se pagó por completo no se suma a la vieja pagada")
    void findGroupedPayables_newPayableAfterFullyPaidOne_isNotMergedWithPaidPayable() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        LocalDateTime now = LocalDateTime.now();
        Payable oldPaid = aProviderPayable(1L, 5L, PayableStatus.PAID, 100, 100, now.minusDays(20));
        Payable newPayable = aProviderPayable(2L, 5L, PayableStatus.PENDING, 50, 0, now.minusDays(1));
        when(payablePersistencePort.findAllByFilters(BUSINESS_ID, null, null, null))
                .thenReturn(List.of(oldPaid, newPayable));

        List<Payable> result = payableService.findGroupedPayables(BUSINESS_ID, null, null);

        assertThat(result).hasSize(2);
        Payable open = result.stream().filter(p -> p.getStatus() == PayableStatus.PENDING).findFirst().orElseThrow();
        assertThat(open.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(50));
        assertThat(open.getPaidAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(open.getCreatedAt()).isEqualTo(newPayable.getCreatedAt());
        Payable closed = result.stream().filter(p -> p.getStatus() == PayableStatus.PAID).findFirst().orElseThrow();
        assertThat(closed.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Las cuentas por pagar abiertas del mismo proveedor siguen sumándose en una sola fila")
    void findGroupedPayables_openPayablesOfSameProvider_areMergedIntoOneRow() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        LocalDateTime now = LocalDateTime.now();
        Payable first = aProviderPayable(1L, 5L, PayableStatus.PARTIAL, 100, 40, now.minusDays(10));
        Payable second = aProviderPayable(2L, 5L, PayableStatus.PENDING, 50, 0, now.minusDays(2));
        when(payablePersistencePort.findAllByFilters(BUSINESS_ID, null, null, null))
                .thenReturn(List.of(first, second));

        List<Payable> result = payableService.findGroupedPayables(BUSINESS_ID, null, null);

        assertThat(result).hasSize(1);
        Payable summary = result.get(0);
        assertThat(summary.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(150));
        assertThat(summary.getPaidAmount()).isEqualByComparingTo(BigDecimal.valueOf(40));
        assertThat(summary.getStatus()).isEqualTo(PayableStatus.PARTIAL);
        assertThat(summary.getCreatedAt()).isEqualTo(first.getCreatedAt());
    }

    @Test
    @DisplayName("Una cuenta por pagar cancelada no se suma al saldo abierto del proveedor")
    void findGroupedPayables_cancelledPayable_isNotIncludedInOpenBalance() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        LocalDateTime now = LocalDateTime.now();
        Payable cancelled = aProviderPayable(1L, 5L, PayableStatus.CANCELLED, 500, 0, now.minusDays(9));
        Payable open = aProviderPayable(2L, 5L, PayableStatus.PENDING, 60, 0, now.minusDays(1));
        when(payablePersistencePort.findAllByFilters(BUSINESS_ID, null, null, null))
                .thenReturn(List.of(cancelled, open));

        List<Payable> result = payableService.findGroupedPayables(BUSINESS_ID, PayableStatus.PENDING, null);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(60));
    }
}
