package com.uzum.transactionprocessing.config;

import com.uzum.transactionprocessing.config.property.CoreLedgerProperties;
import com.uzum.transactionprocessing.handler.RestClientExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class RestClientConfiguration {

    private final CoreLedgerProperties ledgerProperties;

    @Bean(name = "coreLedgerRestClient")
    public RestClient coreLedgerRestClient(RestClient.Builder builder) {
        return builder
            .requestFactory(clientHttpRequestFactory())
            .baseUrl(ledgerProperties.getUrl())
            .defaultStatusHandler(new RestClientExceptionHandler())
            .build();
    }


    @Bean
    public RestClient restClient(RestClient.Builder builder) {
        return builder
            .requestFactory(clientHttpRequestFactory())
            .defaultStatusHandler(new RestClientExceptionHandler())
            .build();
    }

    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        var settings = HttpClientSettings
            .defaults()
            .withReadTimeout(Duration.ofSeconds(15))
            .withConnectTimeout(Duration.ofSeconds(20));

        return ClientHttpRequestFactoryBuilder.jdk().build(settings);
    }
}
