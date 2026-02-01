package com.uzum.transactionprocessing.config.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.retrytopic.DeadLetterPublishingRecovererFactory;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationSupport;

import java.util.function.Consumer;

@Slf4j
@EnableKafka
@Configuration
public class KafkaRetryTopicConfig extends RetryTopicConfigurationSupport {
    @Override
    protected void configureCustomizers(CustomizersConfigurer customizersConfigurer) {
        customizersConfigurer.customizeErrorHandler(defaultErrorHandler ->
            defaultErrorHandler.setLogLevel(KafkaException.Level.WARN)
        );
    }

    @Override
    protected Consumer<DeadLetterPublishingRecovererFactory> configureDeadLetterPublishingContainerFactory() {
        return DeadLetterPublishingRecovererFactory::neverLogListenerException;
    }
}
