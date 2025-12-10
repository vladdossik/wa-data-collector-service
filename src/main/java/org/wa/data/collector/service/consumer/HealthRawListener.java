package org.wa.data.collector.service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.wa.data.collector.service.handler.MessageHandler;
import org.wa.data.collector.service.handler.MessageHandlerChain;
import org.wa.data.collector.service.handler.ProcessingContext;
import org.wa.data.collector.service.model.HealthRawData;

@Slf4j
@Component
public class HealthRawListener {
    private final MessageHandler messageHandlerChain;

    public HealthRawListener(MessageHandlerChain messageHandlerChain) {
        this.messageHandlerChain = messageHandlerChain.getChain();
    }

    @KafkaListener(topics = "${health.topics.raw}", groupId = "${spring.kafka.consumer.group-id}")
    public void receiveRawMessage(ConsumerRecord<String, HealthRawData> record) {
        try {
            ProcessingContext context = ProcessingContext.fromRaw(record.value());
            messageHandlerChain.handle(record, context);
        } catch (Exception e) {
            log.error("Unexpected error processing message at offset {}: {}", 
                    record.offset(), e.getMessage(), e);
            throw e;
        }
    }
}
