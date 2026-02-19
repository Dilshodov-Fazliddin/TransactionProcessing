package com.uzum.transactionprocessing.constant.enums;

import lombok.Getter;

@Getter
public enum Error {

    INTERNAL_SERVICE_ERROR_CODE(10001, "System not available"),
    EXTERNAL_SERVICE_FAILED_ERROR_CODE(10002, "External service not available"),
    HANDLER_NOT_FOUND_ERROR_CODE(10003, "Handler not found"),
    JSON_NOT_VALID_ERROR_CODE(10004, "Json not valid"),
    VALIDATION_ERROR_CODE(10005, "Validation error"),
    INVALID_REQUEST_PARAM_ERROR_CODE(10006, "Invalid request param"),
    INTERNAL_TIMEOUT_ERROR_CODE(10007, "Internal timeout"),
    METHOD_NOT_SUPPORTED_ERROR_CODE(10008, "Method not supported"),
    MISSING_REQUEST_HEADER_ERROR_CODE(10009, "Missing request header"),
    HTTP_SERVICE_ERROR_CODE(10010, "Service error code"),
    HTTP_CLIENT_ERROR_CODE(10011, "Client error code"),
    TRANSACTION_ID_INVALID_CODE(10012, "Transaction Id invalid"),
    CARD_EXPIRED_CODE(10013, "Token of an expired card provided"),
    ACCOUNT_NOT_ACTIVE_CODE(10014, "Provided token belongs to a non-active account"),
    CMS_REQUEST_INVALID_CODE(10015, "CMS request invalid"),
    CURRENCY_INVALID_CODE(10016, "Currency invalid"),
    AMOUNT_NOT_ENOUGH(10017, "Not enough money"),
    INVALID_ACCOUNT_STATUS(10018,"Account status frozen or blocked"),
    AMOUNT_VALIDATE_REQUEST_INVALID(10019,"Amount request invalid"),
    CALCULATION_INVALID_CODE(10020,"Calculation service error code"),

    REFERENCE_ID_EXISTS_CODE(10030, "Transaction with this referenceId exists");



    final int code;
    final String message;

    Error(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
