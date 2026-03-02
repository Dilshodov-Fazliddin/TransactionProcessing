package com.uzum.transactionprocessing.component.adapter.mms;

import com.uzum.transactionprocessing.config.property.MerchantProperties;
import com.uzum.transactionprocessing.dto.response.TerminalResponse;
import com.uzum.transactionprocessing.dto.webhook.TransactionSendWebhook;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class MerchantAdapter {

    RestClient restClient;
    MerchantProperties merchantProperties;

    public TerminalResponse getByTerminalId(Long terminalId) {
        return restClient
                .get()
                .uri(merchantProperties.getUrl()+ "/by-terminal-id/{terminalId}",terminalId)
                .retrieve()
                .body(TerminalResponse.class);
    }


    public void sendToMerchantWebhook(TransactionSendWebhook webhook) {
        try {
            restClient.post()
                    .uri(merchantProperties.getWebhookUrl())
                    .body(webhook)
                    .retrieve()
                    .toBodilessEntity();
            log.info("Webhook successfully sent to merchant");
        } catch (Exception e) {
            log.error("Failed to send webhook to merchant", e);
            throw new RuntimeException("Webhook sending failed", e);
        }
    }
}