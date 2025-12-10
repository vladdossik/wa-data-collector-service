package org.wa.data.collector.service.handler;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.wa.data.collector.service.model.HealthRawData;

public interface MessageHandler {
    
    boolean handle(ConsumerRecord<String, HealthRawData> record, 
                   ProcessingContext context);
    
    MessageHandler setNext(MessageHandler next);
}
