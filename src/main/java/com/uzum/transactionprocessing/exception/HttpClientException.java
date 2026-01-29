package com.uzum.transactionprocessing.exception;

import com.uzum.transactionprocessing.constant.enums.ErrorType;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import static com.uzum.transactionprocessing.constant.enums.Error.HTTP_CLIENT_ERROR_CODE;


public class HttpClientException extends ApplicationException {

    public HttpClientException(String message, HttpStatusCode status) {
        super(HTTP_CLIENT_ERROR_CODE.getCode(), message, ErrorType.EXTERNAL, HttpStatus.valueOf(status.value()));
    }
}
