package com.uzum.transactionprocessing.dto.event;

import com.uzum.transactionprocessing.constant.enums.Currency;
import com.uzum.transactionprocessing.constant.enums.TransactionType;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record TransactionLedgerEvent(@NotNull Long transactionId, @NotNull UUID senderAccountId,
                                     @NotNull UUID receiverAccountId, @NotNull TransactionType transactionType,
                                     @NotNull Long amount, @NotNull Long fee,
                                     @NotNull Currency currency) {
}
