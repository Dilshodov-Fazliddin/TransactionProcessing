package com.uzum.transactionprocessing.dto.request;

import com.uzum.transactionprocessing.constant.enums.Currency;

public record CoreLedgerValidationRequest(String token, Long amount, Currency currency) {
}
