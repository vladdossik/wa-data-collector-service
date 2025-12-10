package org.wa.data.collector.service.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.wa.data.collector.service.model.HealthRawData;

@Slf4j
public abstract class AbstractMessageHandler implements MessageHandler {
    protected MessageHandler next;

    @Override
    public MessageHandler setNext(MessageHandler next) {
        this.next = next;
        return next;
    }

    @Override
    public boolean handle(ConsumerRecord<String, HealthRawData> record, ProcessingContext context) {
        boolean shouldContinue = doHandle(record, context);
        
        if (shouldContinue && context.isShouldContinue() && next != null) {
            return next.handle(record, context);
        }
        
        return shouldContinue && context.isShouldContinue();
    }

    protected abstract boolean doHandle(ConsumerRecord<String, HealthRawData> record, ProcessingContext context);
}
