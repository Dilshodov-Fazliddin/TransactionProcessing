package com.uzum.transactionprocessing.exception.kafka.transiets;

public class HttpServerUnavailableException extends TransientException {
    public HttpServerUnavailableException(Exception ex) {
        super(ex);
    }
}
