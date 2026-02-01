package com.uzum.transactionprocessing.component.kafka.consumer;

import com.uzum.transactionprocessing.component.kafka.EventConsumer;
import com.uzum.transactionprocessing.dto.event.TransactionValidateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AmountValidationConsumer implements EventConsumer<TransactionValidateEvent> {
    @Override
    public void listen(TransactionValidateEvent event) {
        // send request to CMS {"accountId": 22}
        // send core ledger {"amount": ""}
    }

    @Override
    @DltHandler
    public void dltHandler(TransactionValidateEvent event, String exceptionMessage) {

    }
}
