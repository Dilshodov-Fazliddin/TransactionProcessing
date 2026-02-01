package com.uzum.transactionprocessing.exception.kafka.nontransiets;

import com.uzum.transactionprocessing.constant.enums.Error;
import com.uzum.transactionprocessing.constant.enums.ErrorType;

public class TransactionInvalidException extends NonTransientException {
    public TransactionInvalidException(Error error) {
        super(error.getCode(), error.getMessage(), ErrorType.INTERNAL, null);
    }
}
