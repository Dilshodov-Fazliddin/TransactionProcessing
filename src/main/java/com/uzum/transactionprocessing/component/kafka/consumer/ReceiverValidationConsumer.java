package com.uzum.transactionprocessing.component.kafka.consumer;

import com.uzum.transactionprocessing.component.adapter.cms.CmsAdapter;
import com.uzum.transactionprocessing.component.kafka.EventConsumer;
import com.uzum.transactionprocessing.component.kafka.producer.TransactionEvenProducer;
import com.uzum.transactionprocessing.constant.KafkaConstants;
import com.uzum.transactionprocessing.constant.enums.Error;
import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import com.uzum.transactionprocessing.dto.event.TransactionValidateEvent;
import com.uzum.transactionprocessing.dto.response.CmsResponse;
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
public class ReceiverValidationConsumer implements EventConsumer<TransactionValidateEvent> {
    TransactionService transactionService;
    CmsAdapter cmsAdapter;
    TransactionEvenProducer evenProducer;


    @RetryableTopic(attempts = "5", backOff = @BackOff(delay = 5000), include = {TransientException.class}, numPartitions = "3", replicationFactor = "1")
    @KafkaListener(topics = KafkaConstants.RECEIVER_VALIDATE_TOPIC, groupId = KafkaConstants.RECEIVER_VALIDATE_GROUP_ID)
    public void listen(@Payload @Valid TransactionValidateEvent event) {
        // get transaction and validate the status
        // before validating the receiver status should be SENDER_INFO_VALIDATED
        TransactionEntity transaction = transactionService.findById(event.transactionId());

        if (!transaction.getStatus().equals(TransactionStatus.SENDER_INFO_VALIDATED)) {
            log.info("Transaction with id: {} is not in the required status: {}", event.transactionId(), TransactionStatus.CREATED);
            return;
        }

        log.info("Validating Receiver for transaction ID: {}", event.transactionId());

        try {

            CmsResponse response = cmsAdapter.getByTokenAndCurrency(transaction.getReceiverToken(), transaction.getCurrency());

            transactionService.storeReceiverAccountId(transaction.getId(), response.amsAccountId());

            // send to the next topic
            evenProducer.publishForAmountValidation(event);

            log.info("Receiver validation passed for Transaction ID: {}", event.transactionId());

        } catch (HttpServerException e) {

            log.info("Http Server Error during Receiver validation: {}", e.getMessage());

            throw new HttpServerUnavailableException(e);

        } catch (HttpClientException e) {

            log.info("Http Client Error during Receiver validation: {}", e.getMessage());

            throw new CredentialsInvalidException(Error.CMS_REQUEST_INVALID_CODE);

        } catch (Exception e) {

            log.info("Unexpected error: {}", e.getMessage());

            throw new NonTransientException(e);

        }
    }

    @Override
    public void dltHandler(TransactionValidateEvent event, @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage) {

        log.info("Transaction event with ID: {}, send to DLT during Receiver validation, with reason: {}", event.transactionId(), exceptionMessage);

        transactionService.changeTransactionStatus(event.transactionId(), TransactionStatus.RECEIVER_INFO_VALIDATION_FAILED);
    }
}
