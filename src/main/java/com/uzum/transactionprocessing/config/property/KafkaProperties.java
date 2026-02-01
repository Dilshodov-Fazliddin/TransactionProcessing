package com.uzum.transactionprocessing.config.property;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
public class KafkaProperties {
    private KafkaProducerProperties producer;

    private KafkaConsumerProperties consumer;
}
