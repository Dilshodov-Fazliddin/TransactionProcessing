package com.uzum.transactionprocessing.config.property;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KafkaProducerProperties {
    private String bootstrapServers;
}
