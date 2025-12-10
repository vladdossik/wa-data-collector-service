package org.wa.data.collector.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;
import org.wa.data.collector.service.model.HealthRawData;

@Slf4j
@Component
public class NullCheckHandler extends AbstractMessageHandler {

    @Override
    protected boolean doHandle(ConsumerRecord<String, HealthRawData> record, ProcessingContext context) {
        HealthRawData raw = record.value();
        if (raw == null) {
            log.warn("Received null payload at offset {}, skipping", record.offset());
            context.setShouldContinue(false);
            return false;
        }
        
        context.setRawData(raw);
        log.debug("Null check passed for message at offset {}", record.offset());
        return true;
    }
}
