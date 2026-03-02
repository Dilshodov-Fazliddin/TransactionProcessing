package com.uzum.transactionprocessing.component.kafka.consumer;

import com.uzum.transactionprocessing.component.adapter.mms.MerchantAdapter;
import com.uzum.transactionprocessing.component.kafka.EventConsumer;
import com.uzum.transactionprocessing.constant.KafkaConstants;
import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import com.uzum.transactionprocessing.dto.event.TransactionResultEvent;
import com.uzum.transactionprocessing.dto.response.TerminalResponse;
import com.uzum.transactionprocessing.dto.webhook.TransactionSendWebhook;
import com.uzum.transactionprocessing.entity.TransactionEntity;
import com.uzum.transactionprocessing.exception.http.HttpServerException;
import com.uzum.transactionprocessing.exception.kafka.transients.TransientException;
import com.uzum.transactionprocessing.mapper.TransactionMapper;
import com.uzum.transactionprocessing.service.TransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class CoreLedgerConsumer implements EventConsumer<TransactionResultEvent> {

    TransactionService transactionService;
    MerchantAdapter merchantAdapter;
    TransactionMapper transactionMapper;

    @RetryableTopic(attempts = "5", backOff = @BackOff(delay = 5000), include = {TransientException.class}, numPartitions = "3", replicationFactor = "1")
    @KafkaListener(topics = KafkaConstants.LEDGER_TRANSACTIONS_RESULT_TOPIC, groupId = KafkaConstants.LEDGER_TRANSACTIONS_GROUP_ID)
    public void listen(TransactionResultEvent event) {
        TransactionEntity transaction = transactionService.findById(event.transactionId());

        if (!transaction.getStatus().equals(TransactionStatus.SUCCESS)) {
            log.info("Transaction with id: {} is not in the required status: {}", event.transactionId(), TransactionStatus.RECEIVER_INFO_VALIDATED);
            return;
        }
        try {

            TerminalResponse terminalResponse = merchantAdapter.getByTerminalId(transaction.getMerchantId());

            TransactionSendWebhook webhook = transactionMapper.toWebhook(transaction,terminalResponse.webhookUrl());

            merchantAdapter.sendToMerchantWebhook(webhook);

        } catch (HttpServerException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void dltHandler(TransactionResultEvent event, String exceptionMessage) {
        log.info("Transaction event with ID: {}, send to DLT during Receiver validation, with reason: {}", event.transactionId(), exceptionMessage);

        transactionService.changeTransactionStatus(event.transactionId(), TransactionStatus.FAILED);

    }
}

