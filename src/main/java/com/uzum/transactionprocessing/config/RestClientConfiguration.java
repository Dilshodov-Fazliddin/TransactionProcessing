package com.uzum.transactionprocessing.config;

import com.uzum.transactionprocessing.handler.RestClientExceptionHandler;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.HttpClientSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Configuration
public class RestClientConfiguration {
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
