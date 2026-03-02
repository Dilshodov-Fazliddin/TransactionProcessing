package com.uzum.transactionprocessing.dto.event;

import com.uzum.transactionprocessing.constant.enums.TransactionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransactionResultEvent(@NotNull Long transactionId,
                                     @NotNull TransactionStatus transactionStatus,
                                     @NotBlank String errorMessage) {
}
