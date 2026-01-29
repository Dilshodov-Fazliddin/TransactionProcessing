package com.uzum.transactionprocessing.exception;

import com.uzum.transactionprocessing.constant.enums.ErrorType;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@ToString
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationException extends RuntimeException {

    int code;
    String message;
    HttpStatus status;
    ErrorType errorType;

    public ApplicationException(int code, String message, ErrorType errorType, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
        this.message = message;
        this.errorType = errorType;
    }
}
