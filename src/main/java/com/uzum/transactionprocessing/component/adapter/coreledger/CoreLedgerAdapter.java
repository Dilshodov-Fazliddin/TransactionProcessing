package com.uzum.transactionprocessing.component.adapter.coreledger;

import com.uzum.transactionprocessing.config.property.CmsProperties;
import com.uzum.transactionprocessing.config.property.CoreLedgerProperties;
import com.uzum.transactionprocessing.constant.Constants;
import com.uzum.transactionprocessing.constant.enums.Currency;
import com.uzum.transactionprocessing.dto.request.CoreLedgerValidationRequest;
import com.uzum.transactionprocessing.dto.response.CoreLedgerResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CoreLedgerAdapter {
    CoreLedgerProperties coreLedgerProperties;
    RestClient restClient;

    public void validateBalanceByAccountId(String senderToken, Long amount, Currency currency) {
        CoreLedgerValidationRequest request = new CoreLedgerValidationRequest(senderToken, amount, currency);

        restClient
            .post()
            .uri(coreLedgerProperties.getUrl())
            .body(request)
            .retrieve().toBodilessEntity();
    }
}
