package com.uzum.transactionprocessing.dto.request;

import com.uzum.transactionprocessing.constant.enums.Currency;
import com.uzum.transactionprocessing.constant.enums.TransactionType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record TransactionRequest(@NotNull(message = "referenceId required") UUID referenceId,
                                 @NotNull(message = "transaction type required") TransactionType type,
                                 @NotNull(message = "amount is required") @Positive(message = "amount should be positive") Long amount,
                                 @NotNull(message = "currency required") Currency currency,
                                 @NotNull(message = "merchantId required") Long merchantId,
                                 @NotBlank(message = "senderName required") String senderName,
                                 @NotBlank(message = "senderToken required") String senderToken,
                                 @NotBlank(message = "receiverName required") String receiverName,
                                 @NotBlank(message = "receiverToken required") String receiverToken) {


    @AssertTrue(message = "sender and receiver token can't match")
    private boolean isDifferentTokens() {
        return !this.senderToken.equals(this.receiverToken);
    }
}