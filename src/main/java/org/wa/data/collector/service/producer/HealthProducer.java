package org.wa.data.collector.service.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.wa.data.collector.service.model.HealthValidated;
import org.wa.data.collector.service.model.ValidationError;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
public class HealthProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String validatedTopic;
    private final String validatedDataTopic;
    private final String dlqTopic;

    public HealthProducer(KafkaTemplate<String, Object> kafkaTemplate,
                          @Value("${health.topics.validated}") String validatedTopic,
                          @Value("${health.topics.validated_data}") String validatedDataTopic,
                          @Value("${health.topics.dlq}") String dlqTopic) {
        this.kafkaTemplate = kafkaTemplate;
        this.validatedTopic = validatedTopic;
        this.validatedDataTopic = validatedDataTopic;
        this.dlqTopic = dlqTopic;
    }

    public void sendValidated(HealthValidated validated) {
        String userId = validated.getUserId();
        try {
            CompletableFuture<SendResult<String, Object>> future1 = 
                kafkaTemplate.send(validatedTopic, userId, validated);
            CompletableFuture<SendResult<String, Object>> future2 = 
                kafkaTemplate.send(validatedDataTopic, userId, validated);
            
            future1.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send validated data to {} for user: {}", validatedTopic, userId, ex);
                } else {
                    log.debug("Successfully sent validated data to {} for user: {}", validatedTopic, userId);
                }
            });
            
            future2.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send validated data to {} for user: {}", validatedDataTopic, userId, ex);
                } else {
                    log.debug("Successfully sent validated data to {} for user: {}", validatedDataTopic, userId);
                }
            });
        } catch (Exception e) {
            log.error("Error sending validated data for user: {}", userId, e);
            throw e;
        }
    }

    public void sendToDlq(ValidationError error) {
        String userId = error.getUserId();
        try {
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(dlqTopic, userId, error);
            
            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to send error to DLQ for user: {}", userId, ex);
                } else {
                    log.debug("Successfully sent error to DLQ for user: {}", userId);
                }
            });
        } catch (Exception e) {
            log.error("Error sending error to DLQ for user: {}", userId, e);
            throw e;
        }
    }
}
