package com.uzum.transactionprocessing.exception.kafka.transients;

import com.uzum.transactionprocessing.constant.enums.Error;
import com.uzum.transactionprocessing.constant.enums.ErrorType;
import com.uzum.transactionprocessing.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class TransientException extends ApplicationException {

    public TransientException(Exception ex) {
        super(Error.INTERNAL_SERVICE_ERROR_CODE.getCode(), ex.getMessage(), ErrorType.INTERNAL, null);
    }


    public TransientException(int code, String message, ErrorType errorType, HttpStatus status) {
        super(code, message, errorType, status);
    }

}
