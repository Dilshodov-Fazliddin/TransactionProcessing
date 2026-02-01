package com.uzum.transactionprocessing.exception.http;

import com.uzum.transactionprocessing.constant.enums.Error;
import com.uzum.transactionprocessing.constant.enums.ErrorType;
import com.uzum.transactionprocessing.exception.ApplicationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class HttpServerException extends ApplicationException {

    public HttpServerException(String message, HttpStatusCode status) {
        super(Error.HTTP_SERVICE_ERROR_CODE.getCode(), message, ErrorType.EXTERNAL, HttpStatus.valueOf(status.value()));
    }
}
