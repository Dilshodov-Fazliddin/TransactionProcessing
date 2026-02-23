package com.uzum.transactionprocessing.component.adapter.coreledger;

import com.uzum.transactionprocessing.config.property.CoreLedgerProperties;
import com.uzum.transactionprocessing.constant.enums.Currency;
import com.uzum.transactionprocessing.dto.request.CoreLedgerValidationRequest;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CoreLedgerAdapter {
    RestClient coreLedgerRestClient;

    public void validateBalanceByAccountId(UUID amsAccountId, Long amount, Currency currency) {
        CoreLedgerValidationRequest request = new CoreLedgerValidationRequest(amsAccountId, amount, currency);

        coreLedgerRestClient.post().uri("/accounts/validation").body(request).retrieve().toBodilessEntity();
    }
}
