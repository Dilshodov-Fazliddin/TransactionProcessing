package com.uzum.transactionprocessing.dto.response;

import com.uzum.transactionprocessing.constant.enums.Currency;
import com.uzum.transactionprocessing.constant.enums.TransactionStatus;

import java.time.LocalDateTime;

public record TransactionResponse(
    Long transactionId,
    Long referenceId,
    TransactionStatus status,
    Long amount,
    Currency currency,
    LocalDateTime createdAt
) {}