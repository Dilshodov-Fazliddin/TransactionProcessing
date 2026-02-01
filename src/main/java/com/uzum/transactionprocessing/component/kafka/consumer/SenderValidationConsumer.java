package com.uzum.transactionprocessing.component.kafka.consumer;

import com.uzum.transactionprocessing.component.kafka.EventConsumer;
import com.uzum.transactionprocessing.component.kafka.producer.TransactionEvenProducer;
import com.uzum.transactionprocessing.constant.KafkaConstants;
import com.uzum.transactionprocessing.constant.enums.Error;
import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import com.uzum.transactionprocessing.dto.event.TransactionValidateEvent;
import com.uzum.transactionprocessing.entity.TransactionEntity;
import com.uzum.transactionprocessing.exception.http.HttpClientException;
import com.uzum.transactionprocessing.exception.http.HttpServerException;
import com.uzum.transactionprocessing.exception.kafka.nontransiets.CredentialsInvalidException;
import com.uzum.transactionprocessing.exception.kafka.transiets.HttpServerUnavailableException;
import com.uzum.transactionprocessing.exception.kafka.transiets.TransientException;
import com.uzum.transactionprocessing.service.CmsIntegrationService;
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
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true,level = AccessLevel.PUBLIC)
public class SenderValidationConsumer implements EventConsumer<TransactionValidateEvent> {
    TransactionService transactionService;
    CmsIntegrationService cmsIntegrationService;
    TransactionEvenProducer evenProducer;

    @RetryableTopic(attempts = "5", backOff = @BackOff(delay = 5000), include = {TransientException.class}, numPartitions = "3", replicationFactor = "1")
    @KafkaListener(topics = KafkaConstants.SENDER_VALIDATE_TOPIC, groupId = KafkaConstants.SENDER_VALIDATE_GROUP_ID)
    public void listen(@Payload @Valid TransactionValidateEvent event) {
        // claim the transaction for processing to no other consumer processes it
        int claimed = transactionService.claimForProcessing(event.transactionId());

        if (claimed == 0) {
            log.info("Transaction with id: {} is already being processed", event.transactionId());
            return;
        }

        // get transaction and validate the status
        // before validating the sender status should be CREATED
        TransactionEntity transaction = transactionService.findById(event.transactionId());

        if (!transaction.getStatus().equals(TransactionStatus.CREATED)) {
            log.info("Transaction with id: {} is not in the required status: {}", event.transactionId(), TransactionStatus.CREATED);
            return;
        }

        try {
            cmsIntegrationService.validateCardToken(transaction.getSenderToken(), transaction.getCurrency());

            transactionService.changeTransactionStatusAndUnclaim(event.transactionId(), TransactionStatus.SENDER_INFO_VALIDATED);

            // send to the next topic
            evenProducer.publishForReceiverValidation(event);

        } catch (HttpServerException e) {
            transactionService.unclaim(event.transactionId());
            throw new HttpServerUnavailableException(e);

        } catch (HttpClientException e) {
            throw new CredentialsInvalidException(Error.CMS_REQUEST_INVALID_CODE);
        }
    }

    @Override
    @DltHandler
    public void dltHandler(TransactionValidateEvent event, String exceptionMessage) {
        transactionService.changeTransactionStatusAndUnclaim(event.transactionId(), TransactionStatus.SENDER_INFO_VALIDATION_FAILED);
    }
}
