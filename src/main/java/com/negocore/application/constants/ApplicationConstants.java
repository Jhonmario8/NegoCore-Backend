package com.negocore.application.constants;

public class ApplicationConstants {
    public static final String NAME_NOT_BLANK = "El nombre no puede estar vacío";
    public static final String EMAIL_NOT_BLANK = "El correo electrónico no puede estar vacío";
    public static final String PASSWORD_NOT_BLANK = "La contraseña no puede estar vacía";
    public static final String EMAIL_NOT_VALID = "El correo electrónico no es válido";
    public static final String PHONE_NUMBER_NOT_BLANK = "El teléfono no puede estar vacío";
    public static final String CURRENCY_INVALID = "La moneda debe ser una de las siguientes: COP, USD, EUR";
    public static final String CURRENCY_REGEX = "^(COP|USD|EUR)$";
    public static final String CATEGORY_NAME_NOT_BLANK = "El nombre de la categoría no puede estar vacío";
    public static final String CATEGORY_NAME_SIZE = "El nombre de la categoría debe tener entre 2 y 60 caracteres";
    public static final String VALIDATION_PRODUCT_NAME_NOT_BLANK = "El nombre del producto no puede estar vacío";
    public static final String VALIDATION_COST_PRICE_NOT_NULL = "El precio de costo es obligatorio";
    public static final String VALIDATION_SALE_PRICE_NOT_NULL = "El precio de venta es obligatorio";
    public static final String VALIDATION_STOCK_NOT_NULL = "El stock es obligatorio";
    public static final String VALIDATION_COST_PRICE_MIN = "El precio de costo debe ser mayor o igual a 0";
    public static final String VALIDATION_SALE_PRICE_MIN = "El precio de venta debe ser mayor o igual a 1";
    public static final String VALIDATION_STOCK_MIN = "El stock debe ser mayor o igual a 0";
    public static final String VALIDATION_MIN_STOCK_ALERT_NOT_NULL = "La alerta de stock mínimo es obligatoria";
    public static final String VALIDATION_MIN_STOCK_ALERT_MIN = "La alerta de stock mínimo debe ser mayor o igual a 0";
    public static final String REASON_NOT_BLANK = "El motivo no puede estar vacío";
    public static final String REASON_MAX_SIZE = "El motivo no puede superar los 200 caracteres";
    public static final String QUANTITY_NOT_NULL = "La cantidad es obligatoria y no puede ser cero";
    public static final String OPENING_AMOUNT_NOT_NULL = "El monto de apertura es obligatorio";
    public static final String OPENING_AMOUNT_MIN = "El monto de apertura debe ser mayor o igual a 1";
    public static final String PRODUCT_ID_REQUIRED = "El producto es obligatorio";
    public static final String QUANTITY_MUST_BE_POSITIVE = "La cantidad debe ser un número entero positivo";
    public static final String UNIT_PRICE_MUST_BE_POSITIVE = "El precio unitario debe ser mayor a 0";
    public static final String SALE_PRICE_MIN = "El precio de venta debe ser mayor o igual a 0";
    public static final String CREATED_AT_REQUIRED = "La fecha de creación es obligatoria";
    public static final String QUANTITY_REQUIRED = "La cantidad es obligatoria";
    public static final String SALE_ITEMS_REQUIRED = "Los ítems de la venta son obligatorios";
    public static final String PAYMENT_METHOD_REQUIRED = "El método de pago es obligatorio";
    public static final String PAID_AMOUNT_REQUIRED = "El monto pagado es obligatorio";
    public static final String PAID_AMOUNT_MUST_BE_POSITIVE = "El monto pagado debe ser un número positivo o cero";
    public static final String AMOUNT_NOT_NULL = "El monto es obligatorio";
    public static final String AMOUNT_POSITIVE = "El monto debe ser mayor a cero";
    public static final String AMOUNT_NOT_NEGATIVE = "El monto no puede ser negativo";
    public static final String DESCRIPTION_NOT_BLANK = "La descripción no puede estar vacía";
    public static final String DESCRIPTION_SIZE = "La descripción no puede superar los 200 caracteres";
    public static final String CLOSING_AMOUNT_NOT_NULL = "El monto de cierre es obligatorio";
    public static final String CLOSING_AMOUNT_MIN = "El monto de cierre debe ser mayor o igual a 1";

    public static final String CLIENT_NAME_NOT_BLANK = "El nombre del cliente no puede estar vacío";
    public static final String CLIENT_NAME_SIZE = "El nombre del cliente debe tener entre 2 y 100 caracteres";
    public static final String PHONE_REGEX = "^[0-9]{10}$";

    public static final String CLIENT_PHONE_INVALID = "El teléfono del cliente debe ser un número válido de 10 dígitos";
    public static final String CLIENT_EMAIL_INVALID = "El correo del cliente no es válido";
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public static final String PROVIDER_ID_REQUIRED = "El proveedor es obligatorio";
    public static final String PURCHASE_ITEMS_REQUIRED = "Los ítems de la compra son obligatorios";
    public static final String UNIT_COST_REQUIRED = "El costo unitario es obligatorio";
    public static final String UNIT_COST_MIN = "El costo unitario debe ser mayor o igual a 0";
    public static final String SHIPPING_COST_MUST_BE_POSITIVE = "El costo de envío debe ser mayor o igual a 0";
    public static final String CLIENT_NAME_SIZE_QUOTE = "El nombre del cliente no puede superar los 100 caracteres";
    public static final String VALIDITY_DAYS_MIN = "Los días de validez deben ser mayores o iguales a 1";
    public static final String QUOTE_ITEMS_REQUIRED = "Los ítems de la cotización son obligatorios";
    public static final String UNIT_PRICE_MIN = "El precio unitario debe ser mayor o igual a 0";

    public static final String PROVIDER_NAME_NOT_BLANK = "El nombre del proveedor no puede estar vacío";
    public static final String PROVIDER_NAME_SIZE = "El nombre del proveedor debe tener entre 2 y 100 caracteres";
    public static final String PROVIDER_PHONE_INVALID = "El teléfono del proveedor no tiene un formato válido";
    public static final String PROVIDER_EMAIL_INVALID = "El correo del proveedor no es válido";
}
