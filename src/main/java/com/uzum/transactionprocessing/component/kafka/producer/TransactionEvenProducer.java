package com.uzum.transactionprocessing.component.kafka.producer;

import com.uzum.transactionprocessing.constant.KafkaConstants;
import com.uzum.transactionprocessing.dto.event.TransactionValidateEvent;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PRIVATE)
public class TransactionEvenProducer {
    KafkaTemplate<String, Object> kafkaTemplate;

    public void publishForSenderValidation(TransactionValidateEvent event) {
        kafkaTemplate.send(KafkaConstants.SENDER_VALIDATE_TOPIC, event);
    }

    public void publishForReceiverValidation(TransactionValidateEvent event) {
        kafkaTemplate.send(KafkaConstants.RECEIVER_VALIDATE_TOPIC, event);
    }

    public void publishForAmountValidation(TransactionValidateEvent event) {
        kafkaTemplate.send(KafkaConstants.AMOUNT_VALIDATE_TOPIC, event);
    }

    public void publishForCalculateFee(TransactionValidateEvent event) {
        kafkaTemplate.send(KafkaConstants.CALCULATE_FEE, event);
    }
}
