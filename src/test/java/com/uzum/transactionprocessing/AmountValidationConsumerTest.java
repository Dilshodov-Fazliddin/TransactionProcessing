package com.uzum.transactionprocessing;

import com.uzum.transactionprocessing.component.kafka.consumer.AmountValidationConsumer;
import com.uzum.transactionprocessing.component.kafka.producer.TransactionEvenProducer;
import com.uzum.transactionprocessing.constant.enums.Currency;
import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import com.uzum.transactionprocessing.dto.event.TransactionValidateEvent;
import com.uzum.transactionprocessing.entity.TransactionEntity;
import com.uzum.transactionprocessing.exception.http.HttpServerException;
import com.uzum.transactionprocessing.exception.kafka.transients.HttpServerUnavailableException;
import com.uzum.transactionprocessing.service.SenderValidationService;
import com.uzum.transactionprocessing.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AmountValidationConsumerTest {

    @Mock
    private TransactionService transactionService;
    @Mock
    private TransactionEvenProducer evenProducer;
    @Mock
    private SenderValidationService senderValidationService;

    @InjectMocks
    private AmountValidationConsumer amountValidationConsumer;

    private TransactionValidateEvent event;
    private TransactionEntity transaction;

    @BeforeEach
    void setUp() {
        event = new TransactionValidateEvent(1L, OffsetDateTime.now());

        transaction = new TransactionEntity();
        transaction.setId(1L);
        transaction.setStatus(TransactionStatus.RECEIVER_INFO_VALIDATED);
        transaction.setCurrency(Currency.UZS);
        transaction.setAmount(10000L);
        transaction.setSenderToken("sender-token-123");
    }

    @Test
    void listen_ShouldValidateAmountAndPublishEvent_WhenValid() {
        // Arrange
        when(transactionService.claimForProcessing(event.transactionId())).thenReturn(1);
        when(transactionService.findById(event.transactionId())).thenReturn(transaction);

        // Act
        amountValidationConsumer.listen(event);

        // Assert
        verify(senderValidationService).validateAmount(transaction.getSenderToken(), transaction.getAmount(), transaction.getCurrency());
        verify(transactionService).changeTransactionStatusAndUnclaim(transaction.getId(), TransactionStatus.AMOUNT_VALIDATED);
        verify(evenProducer).publishForCalculateFee(event);
    }

    @Test
    void listen_WhenTransactionAlreadyClaimed_ShouldNotProcess() {
        // Arrange
        when(transactionService.claimForProcessing(event.transactionId())).thenReturn(0);

        // Act
        amountValidationConsumer.listen(event);

        // Assert
        // Если транзакция уже занята другим процессом, поиск в БД и валидация не должны вызываться
        verify(transactionService, never()).findById(anyLong());
        verifyNoInteractions(senderValidationService, evenProducer);
    }

    @Test
    void listen_ShouldReturnEarly_WhenStatusIsIncorrect() {
        // Arrange
        transaction.setStatus(TransactionStatus.CREATED); // Неверный статус
        when(transactionService.claimForProcessing(event.transactionId())).thenReturn(1);
        when(transactionService.findById(event.transactionId())).thenReturn(transaction);

        // Act
        amountValidationConsumer.listen(event);

        // Assert
        verify(senderValidationService, never()).validateAmount(anyString(), anyLong(), any());
        verify(evenProducer, never()).publishForCalculateFee(any());
    }

    @Test
    void listen_ShouldThrowTransientException_WhenHttpServerExceptionOccurs() {
        // Arrange
        when(transactionService.claimForProcessing(event.transactionId())).thenReturn(1);
        when(transactionService.findById(event.transactionId())).thenReturn(transaction);
        doThrow(new HttpServerException("Service Unavailable", HttpStatus.SERVICE_UNAVAILABLE)).when(senderValidationService)
                .validateAmount(anyString(), anyLong(), any());

        // Act & Assert
        assertThrows(HttpServerUnavailableException.class, () -> amountValidationConsumer.listen(event));
        // Проверяем, что при ошибке сервера мы "отпускаем" транзакцию (unclaim) для повторной попытки
        verify(transactionService).unclaim(event.transactionId());
    }
}