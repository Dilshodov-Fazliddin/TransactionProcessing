package com.uzum.transactionprocessing.exception.kafka.nontransiets;

import com.uzum.transactionprocessing.constant.enums.Error;
import com.uzum.transactionprocessing.constant.enums.ErrorType;
import com.uzum.transactionprocessing.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class NonTransientException extends ApplicationException {

    public NonTransientException(Exception ex) {
        super(Error.INTERNAL_SERVICE_ERROR_CODE.getCode(), ex.getMessage(), ErrorType.INTERNAL, null);
    }

    public NonTransientException(int code, String message, ErrorType errorType, HttpStatus status) {
        super(code, message, errorType, status);
    }
}
