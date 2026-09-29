package com.negocore.domain.usecase;

import com.negocore.domain.api.IAuthenticationServicePort;
import com.negocore.domain.api.IPurchaseServicePort;
import com.negocore.domain.api.ISaleServicePort;
import com.negocore.domain.constants.DomainConstants;
import com.negocore.domain.exception.BadRequestException;
import com.negocore.domain.exception.NotFoundException;
import com.negocore.domain.model.Business;
import com.negocore.domain.model.Client;
import com.negocore.domain.model.Order;
import com.negocore.domain.model.OrderConversionItemRequest;
import com.negocore.domain.model.OrderConversionRequest;
import com.negocore.domain.model.OrderItem;
import com.negocore.domain.model.OrderItemRequest;
import com.negocore.domain.model.OrderItemSaleRequest;
import com.negocore.domain.model.OrderResponse;
import com.negocore.domain.model.OrderStatus;
import com.negocore.domain.model.PaymentMethod;
import com.negocore.domain.model.Product;
import com.negocore.domain.model.Purchase;
import com.negocore.domain.model.PurchaseRequest;
import com.negocore.domain.model.PurchaseResponse;
import com.negocore.domain.model.Sale;
import com.negocore.domain.model.SaleResponse;
import com.negocore.domain.spi.IAuditLogsPersistencePort;
import com.negocore.domain.spi.IBusinessPersistencePort;
import com.negocore.domain.spi.IClientPersistencePort;
import com.negocore.domain.spi.IOrderItemsPersistencePort;
import com.negocore.domain.spi.IOrderPersistencePort;
import com.negocore.domain.spi.IProductPersistencePort;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long BUSINESS_ID = 10L;
    private static final Long ORDER_ID = 400L;
    private static final Long PRODUCT_ID = 100L;
    private static final Long CLIENT_ID = 55L;

    @Mock private IOrderPersistencePort orderPersistencePort;
    @Mock private IOrderItemsPersistencePort orderItemsPersistencePort;
    @Mock private IAuthenticationServicePort authenticationServicePort;
    @Mock private IBusinessPersistencePort businessPersistencePort;
    @Mock private IProductPersistencePort productPersistencePort;
    @Mock private IClientPersistencePort clientPersistencePort;
    @Mock private IPurchaseServicePort purchaseServicePort;
    @Mock private ISaleServicePort saleServicePort;
    @Mock private IAuditLogsPersistencePort auditLogsPersistencePort;

    @InjectMocks private OrderService orderService;

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

    private Order anOrder(OrderStatus status) {
        Order order = new Order();
        order.setId(ORDER_ID);
        order.setBusinessId(BUSINESS_ID);
        order.setStatus(status);
        return order;
    }

    private Product aProduct(BigDecimal salePrice) {
        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setSalePrice(salePrice);
        return product;
    }

    private OrderItem anItem(Long id, Long clientId, Long convertedSaleId, BigDecimal itemSalePrice) {
        OrderItem item = new OrderItem();
        item.setId(id);
        item.setOrderId(ORDER_ID);
        item.setProductId(PRODUCT_ID);
        item.setQuantity(2);
        item.setClientId(clientId);
        item.setSalePrice(itemSalePrice);
        item.setConvertedSaleId(convertedSaleId);
        return item;
    }

    private void stubOwnedBusiness() {
        when(businessPersistencePort.findById(BUSINESS_ID)).thenReturn(Optional.of(ownedBusiness()));
    }

    // ---- createOrder ----

    @Test
    @DisplayName("createOrder calcula el siguiente número consecutivo y arranca en OPEN")
    void createOrder_incrementsOrderNumber() {
        stubOwnedBusiness();
        when(orderPersistencePort.findMaxOrderNumberByBusinessId(BUSINESS_ID)).thenReturn(4);
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order created = orderService.createOrder(BUSINESS_ID);

        assertThat(created.getOrderNumber()).isEqualTo(5);
        assertThat(created.getStatus()).isEqualTo(OrderStatus.OPEN);
    }

    // ---- addItem ----

    @Test
    @DisplayName("addItem en un pedido que no está OPEN lanza BadRequestException")
    void addItem_orderNotOpen_throwsBadRequest() {
        stubOwnedBusiness();
        when(orderPersistencePort.findByIdAndBusinessId(ORDER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(anOrder(OrderStatus.CONVERTED)));

        OrderItemRequest request = new OrderItemRequest(PRODUCT_ID, 1, null, "Juan", null, null);

        assertThatThrownBy(() -> orderService.addItem(BUSINESS_ID, ORDER_ID, request))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.ORDER_NOT_OPEN);

        verify(orderItemsPersistencePort, never()).save(any());
    }

    @Test
    @DisplayName("addItem con un producto inexistente lanza NotFoundException")
    void addItem_productNotFound_throwsNotFound() {
        stubOwnedBusiness();
        when(orderPersistencePort.findByIdAndBusinessId(ORDER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(anOrder(OrderStatus.OPEN)));
        when(productPersistencePort.findByIdAndBusinessId(PRODUCT_ID, BUSINESS_ID)).thenReturn(Optional.empty());

        OrderItemRequest request = new OrderItemRequest(PRODUCT_ID, 1, null, "Juan", null, null);

        assertThatThrownBy(() -> orderService.addItem(BUSINESS_ID, ORDER_ID, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(DomainConstants.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("addItem con un clientId inexistente lanza NotFoundException")
    void addItem_clientNotFound_throwsNotFound() {
        stubOwnedBusiness();
        when(orderPersistencePort.findByIdAndBusinessId(ORDER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(anOrder(OrderStatus.OPEN)));
        when(productPersistencePort.findByIdAndBusinessId(PRODUCT_ID, BUSINESS_ID))
                .thenReturn(Optional.of(aProduct(BigDecimal.TEN)));
        when(clientPersistencePort.findByIdAndBusinessId(CLIENT_ID, BUSINESS_ID)).thenReturn(Optional.empty());

        OrderItemRequest request = new OrderItemRequest(PRODUCT_ID, 1, CLIENT_ID, null, null, null);

        assertThatThrownBy(() -> orderService.addItem(BUSINESS_ID, ORDER_ID, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(DomainConstants.CLIENT_NOT_FOUND);
    }

    // ---- removeItem ----

    @Test
    @DisplayName("removeItem en un pedido que no está OPEN lanza BadRequestException")
    void removeItem_orderNotOpen_throwsBadRequest() {
        stubOwnedBusiness();
        when(orderPersistencePort.findByIdAndBusinessId(ORDER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(anOrder(OrderStatus.CANCELLED)));

        assertThatThrownBy(() -> orderService.removeItem(BUSINESS_ID, ORDER_ID, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.ORDER_NOT_OPEN);

        verify(orderItemsPersistencePort, never()).deleteByIdAndOrderId(any(), any());
    }

    @Test
    @DisplayName("removeItem con un item inexistente lanza NotFoundException")
    void removeItem_itemNotFound_throwsNotFound() {
        stubOwnedBusiness();
        when(orderPersistencePort.findByIdAndBusinessId(ORDER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(anOrder(OrderStatus.OPEN)));
        when(orderItemsPersistencePort.findByIdAndOrderId(1L, ORDER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.removeItem(BUSINESS_ID, ORDER_ID, 1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage(DomainConstants.ORDER_ITEM_NOT_FOUND);
    }

    // ---- cancelOrder ----

    @Test
    @DisplayName("cancelOrder solo permite cancelar pedidos OPEN")
    void cancelOrder_nonOpenOrder_throwsBadRequest() {
        stubOwnedBusiness();
        when(orderPersistencePort.findByIdAndBusinessId(ORDER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(anOrder(OrderStatus.CONVERTED)));

        assertThatThrownBy(() -> orderService.cancelOrder(BUSINESS_ID, ORDER_ID))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.ORDER_NOT_OPEN);
    }

    @Test
    @DisplayName("cancelOrder marca un pedido OPEN como CANCELLED")
    void cancelOrder_openOrder_setsCancelled() {
        stubOwnedBusiness();
        Order order = anOrder(OrderStatus.OPEN);
        when(orderPersistencePort.findByIdAndBusinessId(ORDER_ID, BUSINESS_ID)).thenReturn(Optional.of(order));
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        Order cancelled = orderService.cancelOrder(BUSINESS_ID, ORDER_ID);

        assertThat(cancelled.getStatus()).isEqualTo(OrderStatus.CANCELLED);
    }

    // ---- convertToPurchase ----

    @Test
    @DisplayName("convertToPurchase agrupa y suma cantidades del mismo producto")
    void convertToPurchase_groupsQuantitiesByProduct() {
        stubOwnedBusiness();
        when(orderPersistencePort.findByIdAndBusinessId(ORDER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(anOrder(OrderStatus.OPEN)));

        OrderItem item1 = anItem(1L, CLIENT_ID, null, null);
        item1.setQuantity(2);
        OrderItem item2 = anItem(2L, null, null, null);
        item2.setQuantity(3);
        when(orderItemsPersistencePort.findAllByOrderId(ORDER_ID)).thenReturn(List.of(item1, item2));

        Purchase purchase = new Purchase();
        purchase.setId(900L);
        when(purchaseServicePort.registerPurchase(any(), any()))
                .thenReturn(new PurchaseResponse(purchase, List.of()));

        OrderConversionRequest conversionRequest = new OrderConversionRequest(
                20L,
                List.of(new OrderConversionItemRequest(PRODUCT_ID, BigDecimal.valueOf(50))),
                PaymentMethod.CASH,
                BigDecimal.valueOf(250),
                null
        );

        orderService.convertToPurchase(BUSINESS_ID, ORDER_ID, conversionRequest);

        ArgumentCaptor<PurchaseRequest> requestCaptor = ArgumentCaptor.forClass(PurchaseRequest.class);
        verify(purchaseServicePort).registerPurchase(eq(BUSINESS_ID), requestCaptor.capture());
        PurchaseRequest sentRequest = requestCaptor.getValue();
        assertThat(sentRequest.purchaseItems()).hasSize(1);
        assertThat(sentRequest.purchaseItems().get(0).quantity()).isEqualTo(5);
        assertThat(sentRequest.purchaseItems().get(0).unitCost()).isEqualByComparingTo(BigDecimal.valueOf(50));
    }

    @Test
    @DisplayName("convertToPurchase sin costo unitario de algún producto lanza BadRequestException")
    void convertToPurchase_missingUnitCost_throwsBadRequest() {
        stubOwnedBusiness();
        when(orderPersistencePort.findByIdAndBusinessId(ORDER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(anOrder(OrderStatus.OPEN)));
        when(orderItemsPersistencePort.findAllByOrderId(ORDER_ID)).thenReturn(List.of(anItem(1L, CLIENT_ID, null, null)));

        OrderConversionRequest conversionRequest = new OrderConversionRequest(
                20L, List.of(), PaymentMethod.CASH, BigDecimal.ZERO, null
        );

        assertThatThrownBy(() -> orderService.convertToPurchase(BUSINESS_ID, ORDER_ID, conversionRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.ORDER_MISSING_UNIT_COST);

        verify(purchaseServicePort, never()).registerPurchase(any(), any());
    }

    @Test
    @DisplayName("convertToPurchase sin items lanza BadRequestException")
    void convertToPurchase_emptyItems_throwsBadRequest() {
        stubOwnedBusiness();
        when(orderPersistencePort.findByIdAndBusinessId(ORDER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(anOrder(OrderStatus.OPEN)));
        when(orderItemsPersistencePort.findAllByOrderId(ORDER_ID)).thenReturn(List.of());

        OrderConversionRequest conversionRequest = new OrderConversionRequest(
                20L, List.of(), PaymentMethod.CASH, BigDecimal.ZERO, null
        );

        assertThatThrownBy(() -> orderService.convertToPurchase(BUSINESS_ID, ORDER_ID, conversionRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.PURCHASE_ITEMS_REQUIRED);
    }

    @Test
    @DisplayName("convertToPurchase en un pedido que no está OPEN lanza BadRequestException")
    void convertToPurchase_nonOpenOrder_throwsBadRequest() {
        stubOwnedBusiness();
        when(orderPersistencePort.findByIdAndBusinessId(ORDER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(anOrder(OrderStatus.CONVERTED)));

        OrderConversionRequest conversionRequest = new OrderConversionRequest(
                20L, List.of(), PaymentMethod.CASH, BigDecimal.ZERO, null
        );

        assertThatThrownBy(() -> orderService.convertToPurchase(BUSINESS_ID, ORDER_ID, conversionRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.ORDER_NOT_OPEN);
    }

    @Test
    @DisplayName("convertToPurchase exitoso delega en PurchaseService y marca el pedido CONVERTED")
    void convertToPurchase_success_marksOrderConverted() {
        stubOwnedBusiness();
        Order order = anOrder(OrderStatus.OPEN);
        when(orderPersistencePort.findByIdAndBusinessId(ORDER_ID, BUSINESS_ID)).thenReturn(Optional.of(order));
        when(orderItemsPersistencePort.findAllByOrderId(ORDER_ID)).thenReturn(List.of(anItem(1L, CLIENT_ID, null, null)));

        Purchase purchase = new Purchase();
        purchase.setId(900L);
        when(purchaseServicePort.registerPurchase(any(), any()))
                .thenReturn(new PurchaseResponse(purchase, List.of()));
        when(orderPersistencePort.save(any(Order.class))).thenAnswer(inv -> inv.getArgument(0));

        OrderConversionRequest conversionRequest = new OrderConversionRequest(
                20L,
                List.of(new OrderConversionItemRequest(PRODUCT_ID, BigDecimal.TEN)),
                PaymentMethod.CASH,
                BigDecimal.ZERO,
                null
        );

        orderService.convertToPurchase(BUSINESS_ID, ORDER_ID, conversionRequest);

        assertThat(order.getStatus()).isEqualTo(OrderStatus.CONVERTED);
        assertThat(order.getConvertedPurchaseId()).isEqualTo(900L);
        assertThat(order.getConvertedAt()).isNotNull();
    }

    // ---- convertItemToSale ----

    @Test
    @DisplayName("convertItemToSale sin cliente en el item lanza BadRequestException")
    void convertItemToSale_noClient_throwsBadRequest() {
        stubOwnedBusiness();
        when(orderPersistencePort.findByIdAndBusinessId(ORDER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(anOrder(OrderStatus.OPEN)));
        when(orderItemsPersistencePort.findByIdAndOrderId(1L, ORDER_ID))
                .thenReturn(Optional.of(anItem(1L, null, null, null)));

        OrderItemSaleRequest saleRequest = new OrderItemSaleRequest(PaymentMethod.CASH, BigDecimal.TEN);

        assertThatThrownBy(() -> orderService.convertItemToSale(BUSINESS_ID, ORDER_ID, 1L, saleRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.ORDER_ITEM_NO_CLIENT);

        verify(saleServicePort, never()).registerSale(any(), any());
    }

    @Test
    @DisplayName("convertItemToSale sobre un item ya vendido lanza BadRequestException")
    void convertItemToSale_alreadySold_throwsBadRequest() {
        stubOwnedBusiness();
        when(orderPersistencePort.findByIdAndBusinessId(ORDER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(anOrder(OrderStatus.OPEN)));
        when(orderItemsPersistencePort.findByIdAndOrderId(1L, ORDER_ID))
                .thenReturn(Optional.of(anItem(1L, CLIENT_ID, 555L, null)));

        OrderItemSaleRequest saleRequest = new OrderItemSaleRequest(PaymentMethod.CASH, BigDecimal.TEN);

        assertThatThrownBy(() -> orderService.convertItemToSale(BUSINESS_ID, ORDER_ID, 1L, saleRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessage(DomainConstants.ORDER_ITEM_ALREADY_SOLD);
    }

    @Test
    @DisplayName("convertItemToSale usa el salePrice guardado en el item si existe")
    void convertItemToSale_usesItemSalePrice_whenPresent() {
        stubOwnedBusiness();
        when(orderPersistencePort.findByIdAndBusinessId(ORDER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(anOrder(OrderStatus.OPEN)));
        OrderItem item = anItem(1L, CLIENT_ID, null, BigDecimal.valueOf(777));
        when(orderItemsPersistencePort.findByIdAndOrderId(1L, ORDER_ID)).thenReturn(Optional.of(item));
        when(productPersistencePort.findByIdAndBusinessId(PRODUCT_ID, BUSINESS_ID))
                .thenReturn(Optional.of(aProduct(BigDecimal.valueOf(999))));

        Sale sale = new Sale();
        sale.setId(88L);
        when(saleServicePort.registerSale(any(), any())).thenReturn(new SaleResponse(sale, List.of()));

        OrderItemSaleRequest saleRequest = new OrderItemSaleRequest(PaymentMethod.CASH, BigDecimal.valueOf(1554));
        orderService.convertItemToSale(BUSINESS_ID, ORDER_ID, 1L, saleRequest);

        ArgumentCaptor<com.negocore.domain.model.SaleRequest> captor =
                ArgumentCaptor.forClass(com.negocore.domain.model.SaleRequest.class);
        verify(saleServicePort).registerSale(any(), captor.capture());
        assertThat(captor.getValue().saleItems().get(0).unitPrice()).isEqualByComparingTo(BigDecimal.valueOf(777));
        assertThat(item.getConvertedSaleId()).isEqualTo(88L);
    }

    @Test
    @DisplayName("convertItemToSale cae al salePrice del producto si el item no tiene uno propio")
    void convertItemToSale_fallsBackToProductSalePrice_whenItemHasNone() {
        stubOwnedBusiness();
        when(orderPersistencePort.findByIdAndBusinessId(ORDER_ID, BUSINESS_ID))
                .thenReturn(Optional.of(anOrder(OrderStatus.OPEN)));
        OrderItem item = anItem(1L, CLIENT_ID, null, null);
        when(orderItemsPersistencePort.findByIdAndOrderId(1L, ORDER_ID)).thenReturn(Optional.of(item));
        when(productPersistencePort.findByIdAndBusinessId(PRODUCT_ID, BUSINESS_ID))
                .thenReturn(Optional.of(aProduct(BigDecimal.valueOf(999))));

        Sale sale = new Sale();
        sale.setId(88L);
        when(saleServicePort.registerSale(any(), any())).thenReturn(new SaleResponse(sale, List.of()));

        OrderItemSaleRequest saleRequest = new OrderItemSaleRequest(PaymentMethod.CASH, BigDecimal.valueOf(1998));
        orderService.convertItemToSale(BUSINESS_ID, ORDER_ID, 1L, saleRequest);

        ArgumentCaptor<com.negocore.domain.model.SaleRequest> captor =
                ArgumentCaptor.forClass(com.negocore.domain.model.SaleRequest.class);
        verify(saleServicePort).registerSale(any(), captor.capture());
        assertThat(captor.getValue().saleItems().get(0).unitPrice()).isEqualByComparingTo(BigDecimal.valueOf(999));
    }
}
