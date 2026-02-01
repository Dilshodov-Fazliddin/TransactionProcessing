package com.uzum.transactionprocessing.component.kafka;

public interface EventConsumer<E> {
    void listen(final E event);

    void dltHandler(E event, String exceptionMessage);
}
