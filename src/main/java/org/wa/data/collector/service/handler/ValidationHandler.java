package org.wa.data.collector.service.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.wa.data.collector.service.model.HealthRawData;
import org.wa.data.collector.service.model.ValidationResult;
import org.wa.data.collector.service.service.HealthValidationService;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationHandler extends AbstractMessageHandler {
    
    private final HealthValidationService validationService;

    @Override
    protected boolean doHandle(ConsumerRecord<String, HealthRawData> record, ProcessingContext context) {
        HealthRawData raw = context.getRawData();
        if (raw == null) {
            log.warn("Cannot validate: raw data is null");
            context.setShouldContinue(false);
            return false;
        }

        ValidationResult result = validationService.validateAndEnrich(raw);
        context.setValidationResult(result);
        
        if (result.isValid()) {
            context.setValidatedData(result.getValidated());
            log.info("Validation passed for user: {}", raw.getUserId());
        } else {
            log.warn("Validation failed for user: {} - {}", raw.getUserId(), result.getMessage());
        }
        
        return true;
    }
}

