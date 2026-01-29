package com.uzum.transactionprocessing.dto.response;

import com.uzum.transactionprocessing.constant.enums.TransactionCurrency;
import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import java.time.LocalDateTime;

public record TransactionResponse(
        Long id,
        Long referenceId,
        TransactionStatus transactionStatus,
        Long amount,
        TransactionCurrency transactionCurrency,
        Long fee,
        String senderName,
        String receiverName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}