package com.uzum.transactionprocessing.exception.kafka.transients;

public class HttpServerUnavailableException extends TransientException {
    public HttpServerUnavailableException(Exception ex) {
        super(ex);
    }
}
