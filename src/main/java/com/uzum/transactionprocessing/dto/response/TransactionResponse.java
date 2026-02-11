package com.uzum.transactionprocessing.dto.response;

import com.uzum.transactionprocessing.constant.enums.Currency;
import com.uzum.transactionprocessing.constant.enums.TransactionStatus;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public record TransactionResponse(
    Long id,
    Long referenceId,
    TransactionStatus status,
    Long amount,
    Currency currency,
    OffsetDateTime createdAt
) {}