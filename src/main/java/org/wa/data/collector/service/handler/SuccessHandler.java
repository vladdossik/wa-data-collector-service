package org.wa.data.collector.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.wa.data.collector.service.model.HealthRawData;
import org.wa.data.collector.service.producer.HealthProducer;

@Slf4j
@Component
public class SuccessHandler extends AbstractMessageHandler {
    
    private final HealthProducer producer;

    public SuccessHandler(HealthProducer producer) {
        this.producer = producer;
    }

    @Override
    protected boolean doHandle(ConsumerRecord<String, HealthRawData> record, ProcessingContext context) {
        if (context.getValidationResult() != null && context.getValidationResult().isValid() && context.getValidatedData() != null) {
            producer.sendValidated(context.getValidatedData());
        }

        return true;
    }
}

