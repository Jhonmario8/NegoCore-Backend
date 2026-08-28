package com.negocore.application.constants;

public class ApplicationConstants {
    public static final String NAME_NOT_BLANK = "name must not be blank";
    public static final String EMAIL_NOT_BLANK = "email must not be blank";
    public static final String PASSWORD_NOT_BLANK = "password must not be blank";
    public static final String EMAIL_NOT_VALID = "email must be a valid email address";
    public static final String PHONE_NUMBER_NOT_BLANK = "phone number must not be blank";
    public static final String CURRENCY_INVALID = "currency must be one of the following: COP, USD, EUR";
    public static final String CURRENCY_REGEX = "^(COP|USD|EUR)$";
    public static final String CATEGORY_NAME_NOT_BLANK = "category name must not be blank";
    public static final String CATEGORY_NAME_SIZE = "category name must be between 2 and 60 characters";
    public static final String VALIDATION_PRODUCT_NAME_NOT_BLANK = "product name must not be blank";
    public static final String VALIDATION_COST_PRICE_NOT_NULL = "costPrice must not be null";
    public static final String VALIDATION_SALE_PRICE_NOT_NULL = "salePrice must not be null";
    public static final String VALIDATION_STOCK_NOT_NULL = "stock must not be null";
    public static final String VALIDATION_COST_PRICE_MIN = "costPrice must be greater than or equal to 0";
    public static final String VALIDATION_SALE_PRICE_MIN = "salePrice must be greater than or equal to 1";
    public static final String VALIDATION_STOCK_MIN = "stock must be greater than or equal to 0";
    public static final String VALIDATION_MIN_STOCK_ALERT_NOT_NULL = "minStockAlert must not be null";
    public static final String VALIDATION_MIN_STOCK_ALERT_MIN = "minStockAlert must be greater than or equal to 0";
}
