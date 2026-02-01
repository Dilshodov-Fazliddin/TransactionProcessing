package com.uzum.transactionprocessing.component.kafka.consumer;

import com.uzum.transactionprocessing.component.kafka.EventConsumer;
import com.uzum.transactionprocessing.constant.KafkaConstants;
import com.uzum.transactionprocessing.dto.event.TransactionValidateEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ValidatedTransactionConsumer implements EventConsumer<TransactionValidateEvent> {
    @Override
    @KafkaListener(topics = KafkaConstants.SENDER_VALIDATE_TOPIC, groupId = KafkaConstants.SENDER_VALIDATE_GROUP_ID)
    public void listen(TransactionValidateEvent event) {

    }

    @Override
    public void dltHandler(TransactionValidateEvent event, String exceptionMessage) {

    }
}
