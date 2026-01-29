package com.uzum.transactionprocessing.dto.request;

import com.uzum.transactionprocessing.constant.enums.TransactionCurrency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransactionRequest(
        @NotNull Long referenceId,
        @NotNull Long amount,
        @NotNull TransactionCurrency transactionCurrency,
        @NotNull Long fee,
        @NotBlank String senderName,
        @NotBlank String senderToken,
        @NotBlank String receiverName,
        @NotBlank String receiverToken
) {}