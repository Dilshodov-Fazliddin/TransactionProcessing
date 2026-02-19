package com.uzum.transactionprocessing;

import com.uzum.transactionprocessing.component.adapter.cms.CmsAdapter;
import com.uzum.transactionprocessing.component.kafka.consumer.SenderValidationConsumer;
import com.uzum.transactionprocessing.component.kafka.producer.TransactionEvenProducer;
import com.uzum.transactionprocessing.constant.enums.AccountStatus;
import com.uzum.transactionprocessing.constant.enums.Currency;
import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import com.uzum.transactionprocessing.constant.enums.TransactionType;
import com.uzum.transactionprocessing.dto.event.TransactionValidateEvent;
import com.uzum.transactionprocessing.dto.response.CmsResponse;
import com.uzum.transactionprocessing.entity.TransactionEntity;
import com.uzum.transactionprocessing.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SenderValidationConsumerTest {
    @Mock
    private TransactionService transactionService;

    @Mock
    private CmsAdapter cmsAdapter;

    @Mock
    private TransactionEvenProducer evenProducer;

    @InjectMocks
    private SenderValidationConsumer senderValidationConsumer;

    private TransactionValidateEvent event;
    private TransactionEntity transaction;

    @BeforeEach
    void setUp() {
        event = new TransactionValidateEvent(1L);

        transaction = new TransactionEntity();
        transaction.setId(1L);
        transaction.setStatus(TransactionStatus.CREATED);
        transaction.setCurrency(Currency.UZS);
        transaction.setSenderToken("token-1");
        transaction.setReceiverToken("token-2");
        transaction.setReferenceId(UUID.randomUUID());
        transaction.setType(TransactionType.P2P);
        transaction.setAmount(10000L);
        transaction.setSenderToken("sender-token-123");
    }

    @Test
    void listen_WhenValidationSuccessful_ShouldUpdateStatusAndPublishEvent() {
        // Arrange
        when(transactionService.findById(transaction.getId())).thenReturn(transaction);
        doNothing().when(cmsAdapter).validateByTokenAndCurrency(transaction.getSenderToken(), transaction.getCurrency());

        // Act
        senderValidationConsumer.listen(event);

        // Assert
        verify(transactionService).findById(transaction.getId());
        verify(cmsAdapter).validateByTokenAndCurrency(transaction.getSenderToken(), transaction.getCurrency());
        verify(transactionService).changeTransactionStatus(transaction.getId(), TransactionStatus.SENDER_INFO_VALIDATED);
        verify(evenProducer).publishForReceiverValidation(event);
    }

    @Test
    void listen_WhenStatusValidated_ShouldSkip() {
        transaction.setStatus(TransactionStatus.SENDER_INFO_VALIDATED);
        when(transactionService.findById(event.transactionId())).thenReturn(transaction);

        senderValidationConsumer.listen(event);

        verify(transactionService).findById(event.transactionId());
        verifyNoInteractions(cmsAdapter, evenProducer);
    }
}
