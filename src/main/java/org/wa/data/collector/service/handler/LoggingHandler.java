package org.wa.data.collector.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.wa.data.collector.service.model.HealthRawData;

@Slf4j
@Component
public class LoggingHandler extends AbstractMessageHandler {

    @Override
    protected boolean doHandle(ConsumerRecord<String, HealthRawData> record, ProcessingContext context) {
        log.info("Received message from topic {}: offset={}, key={}", 
                record.topic(), record.offset(), record.key());
        
        HealthRawData raw = context.getRawData();
        if (raw != null) {
            String userId = raw.getUserId();
            log.debug("Processing health data for user: {} (userId is null: {})", userId, userId == null);
        }
        
        return true;
    }
}
