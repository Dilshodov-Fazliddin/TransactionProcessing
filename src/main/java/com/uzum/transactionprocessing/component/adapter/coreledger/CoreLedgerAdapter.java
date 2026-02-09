package com.uzum.transactionprocessing.component.adapter.coreledger;

import com.uzum.transactionprocessing.constant.Constants;
import com.uzum.transactionprocessing.dto.response.CoreLedgerResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE,makeFinal = true)
public class CoreLedgerAdapter {

    RestClient restClient;

    public CoreLedgerResponse fetchBalanceByAccountId(Long accountId){
        return restClient
                .get()
                .uri(Constants.coreLedgerUrl + accountId)
                .retrieve()
                .body(CoreLedgerResponse.class);
    }
}
