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
import com.uzum.transactionprocessing.exception.kafka.nontransients.CredentialsInvalidException;
import com.uzum.transactionprocessing.exception.kafka.transients.HttpServerUnavailableException;
import com.uzum.transactionprocessing.exception.kafka.transients.TransientException;
import com.uzum.transactionprocessing.service.CmsIntegrationService;
import com.uzum.transactionprocessing.service.TransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReceiverValidationConsumer implements EventConsumer<TransactionValidateEvent> {
    private final TransactionService transactionService;
    private final CmsIntegrationService cmsIntegrationService;
    private final TransactionEvenProducer evenProducer;


    @RetryableTopic(attempts = "5", backOff = @BackOff(delay = 5000), include = {TransientException.class}, numPartitions = "3", replicationFactor = "1")
    @KafkaListener(topics = KafkaConstants.RECEIVER_VALIDATE_TOPIC, groupId = KafkaConstants.RECEIVER_VALIDATE_GROUP_ID)
    public void listen(TransactionValidateEvent event) {
        int claimed = transactionService.claimForProcessing(event.transactionId());

        if (claimed == 0) {
            log.info("Transaction with id: {} is already being processed", event.transactionId());
            return;
        }

        // get transaction and validate the status
        // before validating the receiver status should be SENDER_INFO_VALIDATED
        TransactionEntity transaction = transactionService.findById(event.transactionId());

        if (!transaction.getStatus().equals(TransactionStatus.SENDER_INFO_VALIDATED)) {
            log.info("Transaction with id: {} is not in the required status: {}", event.transactionId(), TransactionStatus.CREATED);
            return;
        }

        try {
            cmsIntegrationService.validateCardToken(transaction.getReceiverToken(), transaction.getCurrency());

            transactionService.changeTransactionStatusAndUnclaim(event.transactionId(), TransactionStatus.RECEIVER_INFO_VALIDATED);

            // send to the next topic
            evenProducer.publishForAmountValidation(event);

        } catch (HttpServerException e) {
            transactionService.unclaim(event.transactionId());
            throw new HttpServerUnavailableException(e);

        } catch (HttpClientException e) {
            throw new CredentialsInvalidException(Error.CMS_REQUEST_INVALID_CODE);
        }
    }

    @Override
    public void dltHandler(TransactionValidateEvent event, String exceptionMessage) {
        transactionService.changeTransactionStatusAndUnclaim(event.transactionId(), TransactionStatus.RECEIVER_INFO_VALIDATION_FAILED);
    }
}
