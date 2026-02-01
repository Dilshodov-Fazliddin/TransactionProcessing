package com.uzum.transactionprocessing.service;

import com.uzum.transactionprocessing.constant.enums.Currency;
import com.uzum.transactionprocessing.dto.response.CoreLedgerResponse;

public interface CoreLedgerIntegrationService {

    CoreLedgerResponse fetchBalanceByAccountId(Long accountId);

}
