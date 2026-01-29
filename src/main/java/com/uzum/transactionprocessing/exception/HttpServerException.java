package com.uzum.transactionprocessing.exception;

import com.uzum.transactionprocessing.constant.enums.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import static com.uzum.transactionprocessing.constant.enums.Error.HTTP_SERVICE_ERROR_CODE;


public class HttpServerException extends ApplicationException {

    public HttpServerException(String message, HttpStatusCode status) {
        super(HTTP_SERVICE_ERROR_CODE.getCode(), message, ErrorType.EXTERNAL, HttpStatus.valueOf(status.value()));
    }
}
