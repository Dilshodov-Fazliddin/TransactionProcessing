package com.uzum.transactionprocessing.service.impl;

import com.uzum.transactionprocessing.dto.response.CoreLedgerResponse;
import com.uzum.transactionprocessing.service.CoreLedgerIntegrationService;
import org.springframework.stereotype.Service;

@Service
public class CoreLedgerIntegrationServiceImpl implements CoreLedgerIntegrationService {

    @Override
    public CoreLedgerResponse fetchBalanceByAccountId(Long accountId) {
        //Http Request
        return null;
    }

}
