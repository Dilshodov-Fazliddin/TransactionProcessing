package com.uzum.transactionprocessing.dto.request;

import com.uzum.transactionprocessing.constant.enums.Currency;

import java.util.UUID;

public record CoreLedgerValidationRequest(UUID amsAccountId, Long amount, Currency currency) {
}
