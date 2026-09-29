package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.ConflictException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.Business;
import com.negocore.domain.model.Debt;
import com.negocore.domain.model.DebtStatus;
import com.negocore.domain.model.PaymentMethod;
import com.negocore.domain.model.Product;
import com.negocore.domain.model.Sale;
import com.negocore.domain.model.SaleItem;
import com.negocore.domain.model.SaleItemRequest;
import com.negocore.domain.model.SaleRequest;
import com.negocore.domain.model.SaleResponse;
import com.negocore.domain.model.SaleStatus;
import com.negocore.domain.spi.IAuditLogsPersistencePort;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.domain.spi.IDebtPaymentPersistencePort;
import com.negocore.domain.spi.IDebtPersistencePort;
import com.negocore.domain.spi.IProductPersistencePort;
import com.negocore.domain.spi.ISaleItemsPersistencePort;
import com.negocore.domain.spi.ISalePersistencePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SaleServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long BUSINESS_ID = 10L;
    private static final Long PRODUCT_ID = 100L;

    @Mock private ISalePersistencePort salePersistencePort;
    @Mock private IAuthenticationServicePort authenticationServicePort;
    @Mock private IBusinessPersistencePort businessPersistencePort;
    @Mock private IProductPersistencePort productPersistencePort;
    @Mock private ISaleItemsPersistencePort saleItemsPersistencePort;
    @Mock private IDebtPersistencePort debtPersistencePort;
    @Mock private IDebtPaymentPersistencePort debtPaymentPersistencePort;
    @Mock private IAuditLogsPersistencePort auditLogsPersistencePort;

    @InjectMocks private SaleService saleService;

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

    private Product aProduct(int stock, BigDecimal salePrice) {
        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setStock(stock);
        product.setSalePrice(salePrice);
        return product;
    }

    private SaleRequest aSaleRequest(int quantity, BigDecimal paidAmount, Long clientId) {
        return new SaleRequest(
                List.of(new SaleItemRequest(PRODUCT_ID, quantity, null)),
                PaymentMethod.CASH,
                paidAmount,
                clientId,
                null
        );
    }

    @Test
    @DisplayName("Registrar una venta con stock suficiente descuenta el stock del producto")
    void registerSale_withSufficientStock_decreasesStock() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Product product = aProduct(10, BigDecimal.valueOf(1000));
        when(productPersistencePort.findAllByIdsAndBusinessId(List.of(PRODUCT_ID), BUSINESS_ID))
                .thenReturn(List.of(product));
        when(salePersistencePort.saveSale(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

        SaleRequest request = aSaleRequest(3, BigDecimal.valueOf(3000), null);
        saleService.registerSale(BUSINESS_ID, request);

        ArgumentCaptor<Product> savedProduct = ArgumentCaptor.forClass(Product.class);
        verify(productPersistencePort).saveProduct(savedProduct.capture());
        assertThat(savedProduct.getValue().getStock()).isEqualTo(7);
    }

    @Test
    @DisplayName("Registrar una venta con stock insuficiente lanza ConflictException y no guarda nada")
    void registerSale_withInsufficientStock_throwsAndSavesNothing() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Product product = aProduct(1, BigDecimal.valueOf(1000));
        when(productPersistencePort.findAllByIdsAndBusinessId(List.of(PRODUCT_ID), BUSINESS_ID))
                .thenReturn(List.of(product));

        SaleRequest request = aSaleRequest(5, BigDecimal.valueOf(5000), null);

        assertThatThrownBy(() -> saleService.registerSale(BUSINESS_ID, request))
                .isInstanceOf(ConflictException.class)
                .hasMessage(DomainConstants.INSUFFICIENT_STOCK);

        verify(salePersistencePort, never()).saveSale(any());
        verify(productPersistencePort, never()).saveProduct(any());
    }

    @Test
    @DisplayName("Un usuario que no es dueño del negocio no puede registrar ventas en él")
    void registerSale_nonOwner_throwsNotFound() {
        Business business = ownedBusiness();
        business.setOwnerId(999L);
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(business));

        SaleRequest request = aSaleRequest(1, BigDecimal.ONE, null);

        assertThatThrownBy(() -> saleService.registerSale(BUSINESS_ID, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(DomainConstants.BUSINESS_NOT_FOUND);

        verify(salePersistencePort, never()).saveSale(any());
    }

    @Test
    @DisplayName("Una venta parcial sin cliente lanza BadRequestException")
    void registerSale_partialPaymentWithoutClient_throwsBadRequest() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Product product = aProduct(10, BigDecimal.valueOf(1000));
        when(productPersistencePort.findAllByIdsAndBusinessId(List.of(PRODUCT_ID), BUSINESS_ID))
                .thenReturn(List.of(product));

        SaleRequest request = aSaleRequest(2, BigDecimal.valueOf(500), null);

        assertThatThrownBy(() -> saleService.registerSale(BUSINESS_ID, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.CLIENT_REQUIRED_FOR_PARTIAL_PAYMENT);
    }

    @Test
    @DisplayName("Una venta parcial con cliente crea una deuda PARTIAL")
    void registerSale_partialPaymentWithClient_createsPartialDebt() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Product product = aProduct(10, BigDecimal.valueOf(1000));
        when(productPersistencePort.findAllByIdsAndBusinessId(List.of(PRODUCT_ID), BUSINESS_ID))
                .thenReturn(List.of(product));
        Sale savedSale = new Sale();
        savedSale.setId(500L);
        when(salePersistencePort.saveSale(any(Sale.class))).thenReturn(savedSale);

        SaleRequest request = aSaleRequest(2, BigDecimal.valueOf(500), 42L);
        saleService.registerSale(BUSINESS_ID, request);

        ArgumentCaptor<Debt> debtCaptor = ArgumentCaptor.forClass(Debt.class);
        verify(debtPersistencePort).save(debtCaptor.capture());
        Debt debt = debtCaptor.getValue();
        assertThat(debt.getStatus()).isEqualTo(DebtStatus.PARTIAL);
        assertThat(debt.getSaleId()).isEqualTo(500L);
        assertThat(debt.getDueDate()).isEqualTo(LocalDate.now().plusDays(30));
    }

    @Test
    @DisplayName("saleItems nulo o vacío lanza BadRequestException")
    void registerSale_emptyItems_throwsBadRequest() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        SaleRequest request = new SaleRequest(List.of(), PaymentMethod.CASH, BigDecimal.ZERO, null, null);

        assertThatThrownBy(() -> saleService.registerSale(BUSINESS_ID, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.SALE_ITEMS_REQUIRED);
    }

    @Test
    @DisplayName("Un producto duplicado en los items lanza BadRequestException")
    void registerSale_duplicateProduct_throwsBadRequest() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        SaleRequest request = new SaleRequest(
                List.of(
                        new SaleItemRequest(PRODUCT_ID, 1, null),
                        new SaleItemRequest(PRODUCT_ID, 2, null)
                ),
                PaymentMethod.CASH,
                BigDecimal.TEN,
                null,
                null
        );

        assertThatThrownBy(() -> saleService.registerSale(BUSINESS_ID, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.DUPLICATE_PRODUCT);
    }

    @Test
    @DisplayName("Cancelar una venta PAID repone el stock y la marca CANCELLED")
    void cancelSale_paidSale_restoresStock() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Sale sale = new Sale();
        sale.setId(500L);
        sale.setBusinessId(BUSINESS_ID);
        sale.setStatus(SaleStatus.PAID);
        when(salePersistencePort.findById(500L)).thenReturn(Optional.of(sale));

        SaleItem item = new SaleItem();
        item.setProductId(PRODUCT_ID);
        item.setQuantity(3);
        when(saleItemsPersistencePort.findAllBySaleId(500L)).thenReturn(List.of(item));
        when(debtPersistencePort.findBySaleId(500L)).thenReturn(Optional.empty());

        Product product = aProduct(5, BigDecimal.valueOf(1000));
        when(productPersistencePort.findAllByIds(List.of(PRODUCT_ID))).thenReturn(List.of(product));
        when(salePersistencePort.saveSale(any(Sale.class))).thenAnswer(inv -> inv.getArgument(0));

        saleService.cancelSale(BUSINESS_ID, 500L);

        ArgumentCaptor<List<Product>> savedProducts = ArgumentCaptor.forClass(List.class);
        verify(productPersistencePort).saveAll(savedProducts.capture());
        assertThat(savedProducts.getValue().get(0).getStock()).isEqualTo(8);
        assertThat(sale.getStatus()).isEqualTo(SaleStatus.CANCELLED);
    }

    @Test
    @DisplayName("Cancelar una venta que ya no está PAID/PARTIAL lanza ConflictException")
    void cancelSale_alreadyCancelled_throwsConflict() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Sale sale = new Sale();
        sale.setId(500L);
        sale.setBusinessId(BUSINESS_ID);
        sale.setStatus(SaleStatus.CANCELLED);
        when(salePersistencePort.findById(500L)).thenReturn(Optional.of(sale));

        assertThatThrownBy(() -> saleService.cancelSale(BUSINESS_ID, 500L))
                .isInstanceOf(ConflictException.class)
                .hasMessage(DomainConstants.SALE_ALREADY_CANCELED);

        verify(productPersistencePort, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("Cancelar una venta cuya deuda ya tiene abonos lanza ConflictException")
    void cancelSale_debtWithPayments_throwsConflict() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        Sale sale = new Sale();
        sale.setId(500L);
        sale.setBusinessId(BUSINESS_ID);
        sale.setStatus(SaleStatus.PARTIAL);
        when(salePersistencePort.findById(500L)).thenReturn(Optional.of(sale));
        when(saleItemsPersistencePort.findAllBySaleId(500L)).thenReturn(List.of());

        Debt debt = new Debt();
        debt.setId(900L);
        when(debtPersistencePort.findBySaleId(500L)).thenReturn(Optional.of(debt));
        when(debtPaymentPersistencePort.existsByDebtId(900L)).thenReturn(true);

        assertThatThrownBy(() -> saleService.cancelSale(BUSINESS_ID, 500L))
                .isInstanceOf(ConflictException.class)
                .hasMessage(DomainConstants.SALE_CANNOT_BE_CANCELED_WITH_PAYMENTS);

        verify(salePersistencePort, times(1)).findById(500L);
    }

    @Test
    @DisplayName("Buscar una venta inexistente lanza NotFoundException")
    void findSaleById_notFound_throwsNotFound() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
        when(salePersistencePort.findByIdAndBusinessId(500L, BUSINESS_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> saleService.findSaleById(BUSINESS_ID, 500L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(DomainConstants.SALE_NOT_FOUND);
    }
}
