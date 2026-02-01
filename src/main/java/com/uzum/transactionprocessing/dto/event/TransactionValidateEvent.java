package com.uzum.transactionprocessing.dto.event;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record TransactionValidateEvent(@NotNull Long transactionId, @NotNull OffsetDateTime requestTimestamp) {
}
