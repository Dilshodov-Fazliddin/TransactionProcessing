package com.uzum.transactionprocessing.exception;

import com.uzum.transactionprocessing.constant.enums.Error;
import com.uzum.transactionprocessing.constant.enums.ErrorType;
import org.springframework.http.HttpStatus;

public class ReferenceIdExistsException extends ApplicationException {
    public ReferenceIdExistsException(Error error) {
        super(error.getCode(), error.getMessage(), ErrorType.VALIDATION, HttpStatus.BAD_REQUEST);
    }
}
