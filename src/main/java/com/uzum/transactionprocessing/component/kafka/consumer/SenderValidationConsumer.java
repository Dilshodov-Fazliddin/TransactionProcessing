package com.uzum.transactionprocessing.component.kafka.consumer;

import com.uzum.transactionprocessing.component.adapter.cms.CmsAdapter;
import com.uzum.transactionprocessing.component.kafka.EventConsumer;
import com.uzum.transactionprocessing.component.kafka.producer.TransactionEvenProducer;
import com.uzum.transactionprocessing.constant.KafkaConstants;
import com.uzum.transactionprocessing.constant.enums.Error;
import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import com.uzum.transactionprocessing.dto.event.TransactionValidateEvent;
import com.uzum.transactionprocessing.entity.TransactionEntity;
import com.uzum.transactionprocessing.exception.http.HttpClientException;
import com.uzum.transactionprocessing.exception.http.HttpServerException;
import com.uzum.transactionprocessing.exception.kafka.nontransients.CredentialsInvalidException;
import com.uzum.transactionprocessing.exception.kafka.nontransients.NonTransientException;
import com.uzum.transactionprocessing.exception.kafka.transients.HttpServerUnavailableException;
import com.uzum.transactionprocessing.exception.kafka.transients.TransientException;
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

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SenderValidationConsumer implements EventConsumer<TransactionValidateEvent> {
    TransactionService transactionService;
    CmsAdapter cmsAdapter;
    TransactionEvenProducer evenProducer;

    @KafkaListener(topics = KafkaConstants.SENDER_VALIDATE_TOPIC, groupId = KafkaConstants.SENDER_VALIDATE_GROUP_ID)
    @RetryableTopic(
        attempts = "4",
        backOff = @BackOff(delay = 5000),
        include = {TransientException.class},
        numPartitions = "3",
        replicationFactor = "1"
    )
    public void listen(@Payload @Valid TransactionValidateEvent event) {
        // get transaction and validate the status
        // before validating the sender status should be CREATED
        TransactionEntity transaction = transactionService.findById(event.transactionId());

        if (!transaction.getStatus().equals(TransactionStatus.CREATED)) {
            log.info("Transaction with id: {} is not in the required status: {}", event.transactionId(), TransactionStatus.CREATED);
            return;
        }

        log.info("Validating Sender for transaction ID: {}", event.transactionId());

        try {

            cmsAdapter.validateByTokenAndCurrency(transaction.getSenderToken(), transaction.getCurrency());

            transactionService.changeTransactionStatus(event.transactionId(), TransactionStatus.SENDER_INFO_VALIDATED);

            // send to the next topic
            evenProducer.publishForReceiverValidation(event);

            log.info("Sender validation passed for Transaction ID: {}", event.transactionId());

        } catch (HttpServerException e) {

            log.info("Http Server Error during sender validation: {}", e.getMessage());

            throw new HttpServerUnavailableException(e);

        } catch (HttpClientException e) {

            log.info("Http Client Error during sender validation: {}", e.getMessage());

            throw new CredentialsInvalidException(Error.CMS_REQUEST_INVALID_CODE);

        } catch (Exception e) {

            log.info("Unexpected error: {}", e.getMessage());

            throw new NonTransientException(e);
        }
    }

    @DltHandler
    public void dltHandler(TransactionValidateEvent event, @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage) {

        log.info("Transaction event with ID: {}, send to DLT during sender validation, with reason: {}", event.transactionId(), exceptionMessage);

        try {
            transactionService.changeTransactionStatus(event.transactionId(), TransactionStatus.SENDER_INFO_VALIDATION_FAILED);
        } catch (Exception e) {
            log.info("DB exception while changing transaction status: {}", e.getMessage());
        }
    }
}
