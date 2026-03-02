package com.uzum.transactionprocessing.config.property;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "service.merchant")
public class MerchantProperties {
    private String url;
    private String webhookUrl;
}
