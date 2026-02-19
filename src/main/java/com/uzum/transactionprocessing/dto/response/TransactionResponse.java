package com.uzum.transactionprocessing.dto.response;

import com.uzum.transactionprocessing.constant.enums.Currency;
import com.uzum.transactionprocessing.constant.enums.TransactionStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TransactionResponse(
    Long id,
    UUID referenceId,
    TransactionStatus status,
    Long amount,
    Currency currency,
    OffsetDateTime createdAt
) {}