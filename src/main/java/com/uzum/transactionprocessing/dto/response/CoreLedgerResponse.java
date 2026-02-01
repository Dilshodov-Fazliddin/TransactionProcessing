package com.uzum.transactionprocessing.dto.response;

import com.uzum.transactionprocessing.constant.enums.AccountStatus;
import com.uzum.transactionprocessing.constant.enums.Currency;

public record CoreLedgerResponse(
        AccountStatus accountStatus,
        Currency currency,
        Long currentBalance
) {
}
