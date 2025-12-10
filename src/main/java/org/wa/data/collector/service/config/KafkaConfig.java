package org.wa.data.collector.service.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.CommonErrorHandler;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.lang.NonNull;
import org.springframework.util.backoff.FixedBackOff;

@Slf4j
@Configuration
public class KafkaConfig {

    @Value("${health.topics.raw}")
    private String rawDataTopic;

    @Value("${health.topics.validated}")
    private String validatedDataTopic;

    @Value("${health.topics.dlq}")
    private String dlqDataTopic;

    @Bean
    public NewTopic rawTopic() {
        return new NewTopic(rawDataTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic validatedTopic() {
        return new NewTopic(validatedDataTopic, 1, (short) 1);
    }


    @Bean
    public NewTopic dlqTopic() {
        return new NewTopic(dlqDataTopic, 1, (short) 1);
    }

    @Bean
    public CommonErrorHandler kafkaErrorHandler() {
        return new DefaultErrorHandler(new FixedBackOff(0L, 0L)) {
            @Override
            public void handleOtherException(
                    @NonNull Exception thrownException,
                    @NonNull Consumer<?, ?> consumer,
                    @NonNull org.springframework.kafka.listener.MessageListenerContainer container,
                    boolean batchListener) {
                log.error("Error processing message, skipping: {}", thrownException.getMessage());
                super.handleOtherException(thrownException, consumer, container, batchListener);
            }
        };
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            @NonNull ConsumerFactory<String, Object> consumerFactory,
            @NonNull CommonErrorHandler kafkaErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(kafkaErrorHandler);
        return factory;
    }
}
