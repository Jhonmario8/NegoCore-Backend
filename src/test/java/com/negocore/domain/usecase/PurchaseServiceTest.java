package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.Business;
import com.negocore.domain.model.Payable;
import com.negocore.domain.model.PaymentMethod;
import com.negocore.domain.model.Product;
import com.negocore.domain.model.Provider;
import com.negocore.domain.model.Purchase;
import com.negocore.domain.model.PurchaseItemRequest;
import com.negocore.domain.model.PurchaseRequest;
import com.negocore.domain.model.PurchaseResponse;
import com.negocore.domain.model.PurchaseStatus;
import com.negocore.domain.spi.IAuditLogsPersistencePort;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.domain.spi.IPayablePersistencePort;
import com.negocore.domain.spi.IProductPersistencePort;
import com.negocore.domain.spi.IProviderPersistencePort;
import com.negocore.domain.spi.IPurchaseItemsPersistencePort;
import com.negocore.domain.spi.IPurchasePersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long BUSINESS_ID = 10L;
    private static final Long PROVIDER_ID = 20L;
    private static final Long PRODUCT_ID = 100L;

    @Mock private IPurchasePersistencePort purchasePersistencePort;
    @Mock private IPurchaseItemsPersistencePort purchaseItemsPersistencePort;
    @Mock private IAuthenticationServicePort authenticationServicePort;
    @Mock private IBusinessPersistencePort businessPersistencePort;
    @Mock private IProviderPersistencePort providerPersistencePort;
    @Mock private IProductPersistencePort productPersistencePort;
    @Mock private IPayablePersistencePort payablePersistencePort;
    @Mock private IAuditLogsPersistencePort auditLogsPersistencePort;

    @InjectMocks private PurchaseService purchaseService;

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

    private Provider aProvider() {
        Provider provider = new Provider();
        provider.setId(PROVIDER_ID);
        provider.setBusinessId(BUSINESS_ID);
        return provider;
    }

    private Product aProduct(int stock) {
        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setStock(stock);
        return product;
    }

    private void stubOwnedBusinessAndProvider() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        when(providerPersistencePort.findByIdAndBusinessId(PROVIDER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(aProvider()));
    }

    @Test
    @DisplayName("Una compra aumenta el stock de cada producto")
    void registerPurchase_increasesStock() {
        stubOwnedBusinessAndProvider();
        Product product = aProduct(5);
        when(productPersistencePort.findAllByIdsAndBusinessId(List.of(PRODUCT_ID), BUSINESS_ID))
                .thenReturn(List.of(product));
        when(purchasePersistencePort.savePurchase(any(Purchase.class))).thenAnswer(inv -> inv.getArgument(0));

        PurchaseRequest request = new PurchaseRequest(
                PROVIDER_ID,
                List.of(new PurchaseItemRequest(PRODUCT_ID, 3, BigDecimal.valueOf(100))),
                PaymentMethod.CASH,
                BigDecimal.valueOf(300),
                null,
                null
        );

        purchaseService.registerPurchase(BUSINESS_ID, request);

        ArgumentCaptor<Product> savedProduct = ArgumentCaptor.forClass(Product.class);
        verify(productPersistencePort).saveProduct(savedProduct.capture());
        assertThat(savedProduct.getValue().getStock()).isEqualTo(8);
    }

    @Test
    @DisplayName("Un pago parcial crea una cuenta por pagar pendiente")
    void registerPurchase_partialPayment_createsPayable() {
        stubOwnedBusinessAndProvider();
        Product product = aProduct(5);
        when(productPersistencePort.findAllByIdsAndBusinessId(List.of(PRODUCT_ID), BUSINESS_ID))
                .thenReturn(List.of(product));
        when(purchasePersistencePort.savePurchase(any(Purchase.class))).thenAnswer(inv -> {
            Purchase purchase = inv.getArgument(0);
            purchase.setId(700L);
            return purchase;
        });

        PurchaseRequest request = new PurchaseRequest(
                PROVIDER_ID,
                List.of(new PurchaseItemRequest(PRODUCT_ID, 3, BigDecimal.valueOf(100))),
                PaymentMethod.CASH,
                BigDecimal.valueOf(100),
                null,
                null
        );

        PurchaseResponse response = purchaseService.registerPurchase(BUSINESS_ID, request);

        assertThat(response.getPurchase().getStatus()).isEqualTo(PurchaseStatus.PARTIAL);
        ArgumentCaptor<Payable> payableCaptor = ArgumentCaptor.forClass(Payable.class);
        verify(payablePersistencePort).save(payableCaptor.capture());
        assertThat(payableCaptor.getValue().getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(200));
        assertThat(payableCaptor.getValue().getSourceId()).isEqualTo(700L);
    }

    @Test
    @DisplayName("Un pago completo no crea ninguna cuenta por pagar")
    void registerPurchase_fullPayment_createsNoPayable() {
        stubOwnedBusinessAndProvider();
        Product product = aProduct(5);
        when(productPersistencePort.findAllByIdsAndBusinessId(List.of(PRODUCT_ID), BUSINESS_ID))
                .thenReturn(List.of(product));
        when(purchasePersistencePort.savePurchase(any(Purchase.class))).thenAnswer(inv -> inv.getArgument(0));

        PurchaseRequest request = new PurchaseRequest(
                PROVIDER_ID,
                List.of(new PurchaseItemRequest(PRODUCT_ID, 3, BigDecimal.valueOf(100))),
                PaymentMethod.CASH,
                BigDecimal.valueOf(300),
                null,
                null
        );

        PurchaseResponse response = purchaseService.registerPurchase(BUSINESS_ID, request);

        assertThat(response.getPurchase().getStatus()).isEqualTo(PurchaseStatus.PAID);
        verify(payablePersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("Un proveedor inexistente lanza NotFoundException")
    void registerPurchase_providerNotFound_throwsNotFound() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        when(providerPersistencePort.findByIdAndBusinessId(PROVIDER_ID, BUSINESS_ID)).thenReturn(Optional.empty());

        PurchaseRequest request = new PurchaseRequest(
                PROVIDER_ID, List.of(new PurchaseItemRequest(PRODUCT_ID, 1, BigDecimal.TEN)),
                PaymentMethod.CASH, BigDecimal.TEN, null, null
        );

        assertThatThrownBy(() -> purchaseService.registerPurchase(BUSINESS_ID, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(DomainConstants.PROVIDER_NOT_FOUND);
    }

    @Test
    @DisplayName("purchaseItems vacío lanza BadRequestException")
    void registerPurchase_emptyItems_throwsBadRequest() {
        stubOwnedBusinessAndProvider();

        PurchaseRequest request = new PurchaseRequest(
                PROVIDER_ID, List.of(), PaymentMethod.CASH, BigDecimal.ZERO, null, null
        );

        assertThatThrownBy(() -> purchaseService.registerPurchase(BUSINESS_ID, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.PURCHASE_ITEMS_REQUIRED);
    }

    @Test
    @DisplayName("Un paidAmount mayor al total lanza BadRequestException")
    void registerPurchase_paidAmountExceedsTotal_throwsBadRequest() {
        stubOwnedBusinessAndProvider();
        when(productPersistencePort.findAllByIdsAndBusinessId(List.of(PRODUCT_ID), BUSINESS_ID))
                .thenReturn(List.of(aProduct(5)));

        PurchaseRequest request = new PurchaseRequest(
                PROVIDER_ID,
                List.of(new PurchaseItemRequest(PRODUCT_ID, 1, BigDecimal.valueOf(100))),
                PaymentMethod.CASH,
                BigDecimal.valueOf(9999),
                null,
                null
        );

        assertThatThrownBy(() -> purchaseService.registerPurchase(BUSINESS_ID, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.INVALID_PAID_AMOUNT);
    }

    @Test
    @DisplayName("Un producto del pedido que no existe en el negocio lanza NotFoundException")
    void registerPurchase_productNotFound_throwsNotFound() {
        stubOwnedBusinessAndProvider();
        when(productPersistencePort.findAllByIdsAndBusinessId(List.of(PRODUCT_ID), BUSINESS_ID))
                .thenReturn(List.of());

        PurchaseRequest request = new PurchaseRequest(
                PROVIDER_ID,
                List.of(new PurchaseItemRequest(PRODUCT_ID, 1, BigDecimal.TEN)),
                PaymentMethod.CASH,
                BigDecimal.TEN,
                null,
                null
        );

        assertThatThrownBy(() -> purchaseService.registerPurchase(BUSINESS_ID, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(DomainConstants.PRODUCT_NOT_FOUND);
    }
}
