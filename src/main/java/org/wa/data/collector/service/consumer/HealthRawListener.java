package org.wa.data.collector.service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.wa.data.collector.service.model.HealthRaw;
import org.wa.data.collector.service.model.ValidationError;
import org.wa.data.collector.service.model.ValidationResult;
import org.wa.data.collector.service.producer.HealthProducer;
import org.wa.data.collector.service.service.HealthValidationService;

@Slf4j
@Component
public class HealthRawListener {
    private final HealthValidationService validationService;
    private final HealthProducer producer;

    public HealthRawListener(HealthValidationService validationService, HealthProducer producer) {
        this.validationService = validationService;
        this.producer = producer;
    }

    @KafkaListener(topics = "${health.topics.raw}", groupId = "${spring.kafka.consumer.group-id}")
    public void onMessage(ConsumerRecord<String, HealthRaw> record) {
        try {
            log.info("Received message from topic {}: offset={}, key={}", 
                    record.topic(), record.offset(), record.key());
            
            HealthRaw raw = record.value();
            if (raw == null) {
                log.warn("Received null payload at offset {}, skipping", record.offset());
                return;
            }

            String userId = raw.getUserId();
            log.debug("Processing health data for user: {} (userId is null: {})", userId, userId == null);
            
            ValidationResult result = validationService.validateAndEnrich(raw);
            if (result.isValid()) {
                log.info("Validation passed for user: {}", userId);
                producer.sendValidated(result.getValidated());
            } else {
                log.warn("Validation failed for user: {} - {}", userId, result.getMessage());
                ValidationError error = new ValidationError(
                        userId,
                        result.getMessage(),
                        result.getRawPayload()
                );
                producer.sendToDlq(error);
            }
        } catch (Exception e) {
            log.error("Unexpected error processing message at offset {}: {}", 
                    record.offset(), e.getMessage(), e);
            throw e;
        }
    }
}
