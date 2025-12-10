package org.wa.data.collector.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.wa.data.collector.service.model.HealthRawData;
import org.wa.data.collector.service.model.ValidationError;
import org.wa.data.collector.service.model.ValidationResult;
import org.wa.data.collector.service.producer.HealthProducer;

@Slf4j
@Component
public class ErrorHandler extends AbstractMessageHandler {
    
    private final HealthProducer producer;

    public ErrorHandler(HealthProducer producer) {
        this.producer = producer;
    }

    @Override
    protected boolean doHandle(ConsumerRecord<String, HealthRawData> record, ProcessingContext context) {
        ValidationResult result = context.getValidationResult();
        
        if (result != null && !result.isValid()) {
            HealthRawData raw = context.getRawData();
            String userId = raw != null ? raw.getUserId() : "unknown";
            
            ValidationError error = new ValidationError(
                    userId,
                    result.getMessage(),
                    result.getRawPayload()
            );
            
            context.setError(error);
            producer.sendToDlq(error);
        }
        
        return true;
    }
}

