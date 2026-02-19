package com.uzum.transactionprocessing.exception.kafka.nontransients;

import com.uzum.transactionprocessing.constant.enums.Error;
import com.uzum.transactionprocessing.constant.enums.ErrorType;

public class HttpRequestInvalidException extends NonTransientException {
    public HttpRequestInvalidException(Error error, Exception ex) {
        super(error.getCode(), ex.getMessage(), ErrorType.INTERNAL, null);
    }
}
