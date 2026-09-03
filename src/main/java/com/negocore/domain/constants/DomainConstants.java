package com.negocore.domain.constants;

public class DomainConstants {
    public static final String PASSWORD_NULL_OR_EMPTY = "Password cannot be null or empty";
    public static final String INVALID_PASSWORD_MESSAGE = "Password must be at least 8 characters long, contain at least one uppercase letter and one number.";
    public static final String INVALID_EMAIL_MESSAGE = "Email must be a valid email address.";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_SUBJECT = "sub";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String INVALID_PHONE_NUMBER_MESSAGE = "Phone number must be a valid phone number with 10 to 15 digits, optionally starting with a + sign.";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String PHONE_NUMBER_ALREADY_EXISTS = "Phone number already exists";
    public static final String INVALID_CREDENTIALS = "Invalid credentials";
    public static final String USER_INACTIVE = "User is inactive";
    public static final String BUSINESS_NOT_FOUND = "Business not found";
    public static final String CATEGORY_ALREADY_EXISTS = "Category already exists";
    public static final String SKU_ALREADY_EXISTS = "SKU already exists";
    public static final String PRODUCT_NOT_FOUND = "Product not found";
    public static final String INSUFFICIENT_STOCK = "Insufficient stock";
    public static final String REASON_INVALID = "Reason must not be null, empty, or exceed 200 characters";
    public static final String QUANTITY_INVALID = "Quantity must not be zero";
    public static final String CASH_REGISTER_ALREADY_OPEN = "There is already an open cash register for this business";
    public static final String CASH_REGISTER_NOT_OPEN = "There is no open cash register for this business";
    public static final String DUPLICATE_PRODUCT = "Duplicate product in sale items";
    public static final String CLIENT_REQUIRED_FOR_PARTIAL_PAYMENT = "Client is required for partial payment";
    public static final String INVALID_PAID_AMOUNT = "Paid amount must be greater than or equal to zero";
    public static final String SALE_ITEMS_REQUIRED = "Sale items are required";
    public static final String SALE_CASH_MOVEMENT_DESCRIPTION = "Sale payment for sale ID: ";
    public static final String SALE_NOT_FOUND = "Sale not found";
    public static final String SALE_ALREADY_CANCELED = "Sale is already canceled";
    public static final String SALE_CANNOT_BE_CANCELED_WITH_PAYMENTS = "Sale cannot be canceled because it has associated payments";
    public static String Category_NOT_FOUND = "Category not found";
}
