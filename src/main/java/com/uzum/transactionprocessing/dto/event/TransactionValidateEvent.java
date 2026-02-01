package com.uzum.transactionprocessing.dto.event;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record TransactionValidateEvent
        (
         @NotNull(message = "Transaction id is null") Long transactionId,
         @NotNull(message = "Request time stamp is null ") OffsetDateTime requestTimestamp
        ) {
}
