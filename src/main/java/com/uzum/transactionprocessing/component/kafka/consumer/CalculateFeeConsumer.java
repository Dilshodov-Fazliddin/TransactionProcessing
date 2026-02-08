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
import com.uzum.transactionprocessing.exception.kafka.transients.HttpServerUnavailableException;
import com.uzum.transactionprocessing.exception.kafka.transients.TransientException;
import com.uzum.transactionprocessing.service.CalculateFeeService;
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
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CalculateFeeConsumer implements EventConsumer<TransactionValidateEvent> {

    TransactionService transactionService;
    TransactionEvenProducer evenProducer;
    CalculateFeeService calculateFeeService;

    @RetryableTopic(attempts = "5", backOff = @BackOff(delay = 5000), include = {TransientException.class}, numPartitions = "3", replicationFactor = "1")
    @KafkaListener(topics = KafkaConstants.AMOUNT_VALIDATE_TOPIC, groupId = KafkaConstants.CALCULATE_FEE_GROUP_ID)
    public void listen(TransactionValidateEvent event) {
        int claimed = transactionService.claimForProcessing(event.transactionId());

        if (claimed == 0){
            log.info("Transaction is already being processed with id: {}", event.transactionId());
            return;
        }

        TransactionEntity transaction = transactionService.findById(event.transactionId());

        if (!transaction.getStatus().equals(TransactionStatus.AMOUNT_VALIDATION_FAILED)) {
            log.info("Transaction with id: {} is not in the required status: {}", event.transactionId(), TransactionStatus.AMOUNT_VALIDATION_FAILED);
            return;
        }

        try {

            transactionService.updateFee(transaction.getId(), calculateFeeService.calculateFee(transaction.getAmount()));

            transactionService.changeTransactionStatusAndUnclaim(transaction.getId(),TransactionStatus.PROCESSING_BY_LEDGER);

            evenProducer.publishForCoreLedger(event);

        } catch (HttpServerException e){
            transactionService.unclaim(event.transactionId());
            throw new HttpServerUnavailableException(e);
        }catch (HttpClientException e){
            throw new CredentialsInvalidException(Error.CALCULATION_INVALID_CODE);
        }
    }

    @Override
    @DltHandler
    public void dltHandler(TransactionValidateEvent event, String exceptionMessage) {
        transactionService.changeTransactionStatusAndUnclaim(event.transactionId(), TransactionStatus.CALCULATE_COMPLETED_FAILED);
    }
}
