package com.uzum.transactionprocessing.dto.webhook;

import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record TransactionSendWebhook(
     @NotNull Long transactionId,
     @NotNull Long referenceId,
     @NotNull Long amount,
     @NotNull Long fee,
     @NotNull TransactionStatus status,
     @NotNull String webhookUrl,
     OffsetDateTime createdAt,
     OffsetDateTime updatedAt
) {
}
