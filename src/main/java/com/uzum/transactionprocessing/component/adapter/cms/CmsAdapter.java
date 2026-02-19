package com.uzum.transactionprocessing.component.adapter.cms;

import com.uzum.transactionprocessing.config.property.CmsProperties;
import com.uzum.transactionprocessing.constant.Constants;
import com.uzum.transactionprocessing.constant.enums.Currency;
import com.uzum.transactionprocessing.dto.response.CmsResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CmsAdapter {
    CmsProperties cmsProperties;
    RestClient restClient;

    public void validateByTokenAndCurrency(String token, Currency currency) {
        String requestEndpoint = String.format("%s/%s/%s", cmsProperties.getUrl(), token, currency);

        log.info("Sender validation url: {}", requestEndpoint);

        restClient.get().uri(requestEndpoint).retrieve().toBodilessEntity();
    }
}
