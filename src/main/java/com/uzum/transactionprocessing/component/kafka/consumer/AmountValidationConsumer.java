package com.uzum.transactionprocessing.component.kafka.consumer;

import com.uzum.transactionprocessing.component.adapter.coreledger.CoreLedgerAdapter;
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
public class AmountValidationConsumer implements EventConsumer<TransactionValidateEvent> {

    TransactionService transactionService;
    TransactionEvenProducer evenProducer;
    CoreLedgerAdapter coreLedgerAdapter;

    @RetryableTopic(attempts = "5", backOff = @BackOff(delay = 5000), include = {TransientException.class}, numPartitions = "3", replicationFactor = "1")
    @KafkaListener(topics = KafkaConstants.AMOUNT_VALIDATE_TOPIC, groupId = KafkaConstants.AMOUNT_VALIDATE_GROUP_ID)
    public void listen(@Payload @Valid TransactionValidateEvent event) {
        TransactionEntity transaction = transactionService.findById(event.transactionId());

        if (!transaction.getStatus().equals(TransactionStatus.RECEIVER_INFO_VALIDATED)) {
            log.info("Transaction with id: {} is not in the required status: {}", event.transactionId(), TransactionStatus.RECEIVER_INFO_VALIDATED);
            return;
        }

        try {

            coreLedgerAdapter.validateBalanceByAccountId(transaction.getSenderToken(), transaction.getAmount(), transaction.getCurrency());

            transactionService.changeTransactionStatus(transaction.getId(), TransactionStatus.AMOUNT_VALIDATED);

            evenProducer.publishForCalculateFee(event);

            log.info("Amount validation passed for Transaction ID: {}", event.transactionId());

        } catch (HttpServerException e) {

            log.info("Http Server Error during Amount validation: {}", e.getMessage());

            throw new HttpServerUnavailableException(e);

        } catch (HttpClientException e) {

            log.info("Http Client Error during Amount validation: {}", e.getMessage());

            throw new CredentialsInvalidException(Error.AMOUNT_VALIDATE_REQUEST_INVALID);
        }
    }

    @Override
    @DltHandler
    public void dltHandler(TransactionValidateEvent event, @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage) {

        log.info("Transaction event with ID: {}, send to DLT during Amount validation, with reason: {}", event.transactionId(), exceptionMessage);

        transactionService.changeTransactionStatus(event.transactionId(), TransactionStatus.AMOUNT_VALIDATION_FAILED);
    }
}
