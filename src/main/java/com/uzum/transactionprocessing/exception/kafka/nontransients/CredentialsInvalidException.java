package com.uzum.transactionprocessing.exception.kafka.nontransients;

import com.uzum.transactionprocessing.constant.enums.Error;
import com.uzum.transactionprocessing.constant.enums.ErrorType;

public class CredentialsInvalidException extends NonTransientException {
    public CredentialsInvalidException(Error error) {
        super(error.getCode(), error.getMessage(), ErrorType.INTERNAL, null);
    }
}
