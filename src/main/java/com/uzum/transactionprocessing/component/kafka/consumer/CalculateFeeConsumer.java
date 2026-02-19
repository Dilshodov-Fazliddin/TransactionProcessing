package com.uzum.transactionprocessing.component.kafka.consumer;

import com.uzum.transactionprocessing.component.kafka.EventConsumer;
import com.uzum.transactionprocessing.component.kafka.producer.TransactionEvenProducer;
import com.uzum.transactionprocessing.constant.KafkaConstants;
import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import com.uzum.transactionprocessing.dto.event.TransactionValidateEvent;
import com.uzum.transactionprocessing.entity.TransactionEntity;
import com.uzum.transactionprocessing.exception.kafka.transients.TransientException;
import com.uzum.transactionprocessing.service.CalculateFeeService;
import com.uzum.transactionprocessing.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CalculateFeeConsumer implements EventConsumer<TransactionValidateEvent> {

    TransactionService transactionService;
    TransactionEvenProducer evenProducer;
    CalculateFeeService calculateFeeService;

    @RetryableTopic(attempts = "5", backOff = @BackOff(delay = 5000), include = {TransientException.class}, numPartitions = "3", replicationFactor = "1")
    @KafkaListener(topics = KafkaConstants.CALCULATE_FEE, groupId = KafkaConstants.CALCULATE_FEE_GROUP_ID)
    public void listen(@Payload @Valid TransactionValidateEvent event) {
        TransactionEntity transaction = transactionService.findById(event.transactionId());

        if (!transaction.getStatus().equals(TransactionStatus.AMOUNT_VALIDATED)) {
            log.info("Transaction with id: {} is not in the required status: {}", event.transactionId(), TransactionStatus.AMOUNT_VALIDATION_FAILED);
            return;
        }

        transactionService.updateFee(transaction.getId(), calculateFeeService.calculateFee(transaction.getAmount()));

        transactionService.changeTransactionStatus(transaction.getId(), TransactionStatus.SENT_TO_CORE_LEDGER);

        evenProducer.publishForCoreLedger(event);
    }

    @Override
    @DltHandler
    public void dltHandler(TransactionValidateEvent event, @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage) {

        log.info("Transaction event with ID: {}, send to DLT during Fee calculation, with reason: {}", event.transactionId(), exceptionMessage);

        transactionService.changeTransactionStatus(event.transactionId(), TransactionStatus.CALCULATE_FAILED);
    }
}
