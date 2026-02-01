package com.uzum.transactionprocessing.component.kafka.consumer;

import com.uzum.transactionprocessing.component.kafka.EventConsumer;
import com.uzum.transactionprocessing.component.kafka.producer.TransactionEvenProducer;
import com.uzum.transactionprocessing.constant.KafkaConstants;
import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import com.uzum.transactionprocessing.dto.event.TransactionValidateEvent;
import com.uzum.transactionprocessing.entity.TransactionEntity;
import com.uzum.transactionprocessing.exception.http.HttpServerException;
import com.uzum.transactionprocessing.exception.kafka.transiets.HttpServerUnavailableException;
import com.uzum.transactionprocessing.exception.kafka.transiets.TransientException;
import com.uzum.transactionprocessing.service.SenderValidationService;
import com.uzum.transactionprocessing.service.TransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Slf4j
public class AmountValidationConsumer implements EventConsumer<TransactionValidateEvent> {

    TransactionService transactionService;
    TransactionEvenProducer evenProducer;
    SenderValidationService senderValidationService;

    @RetryableTopic(attempts = "5", backOff = @BackOff(delay = 5000), include = {TransientException.class}, numPartitions = "3", replicationFactor = "1")
    @KafkaListener(topics = KafkaConstants.AMOUNT_VALIDATE_TOPIC, groupId = KafkaConstants.AMOUNT_VALIDATE_GROUP_ID)
    public void listen(TransactionValidateEvent event) {

        int claimed = transactionService.claimForProcessing(event.transactionId());

        if (claimed == 0){
            log.info("Transaction is already being processed with id: {}", event.transactionId());
            return;
        }

        TransactionEntity transaction = transactionService.findById(event.transactionId());

        if (!transaction.getStatus().equals(TransactionStatus.RECEIVER_INFO_VALIDATED)) {
            log.info("Transaction with id: {} is not in the required status: {}", event.transactionId(), TransactionStatus.RECEIVER_INFO_VALIDATED);
            return;
        }

        try {
            senderValidationService.validateAmount(transaction.getSenderToken(),transaction.getAmount(),transaction.getCurrency());

            transactionService.changeTransactionStatusAndUnclaim(transaction.getId(), TransactionStatus.AMOUNT_VALIDATED);

            evenProducer.publishForCalculateFee(event);
        } catch (HttpServerException e) {
            transactionService.unclaim(event.transactionId());
            throw new HttpServerUnavailableException(e);
        }
    }

    @Override
    @DltHandler
    public void dltHandler(TransactionValidateEvent event, String exceptionMessage) {
        transactionService.changeTransactionStatusAndUnclaim(event.transactionId(), TransactionStatus.AMOUNT_VALIDATION_FAILED);
    }
}
