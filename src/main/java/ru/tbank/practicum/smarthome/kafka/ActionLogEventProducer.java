package ru.tbank.practicum.smarthome.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.tbank.practicum.smarthome.event.ActionLogEvent;

@Component
public class ActionLogEventProducer {

    private static final Logger log = LoggerFactory.getLogger(ActionLogEventProducer.class);

    private final KafkaTemplate<String, ActionLogEvent> kafkaTemplate;

    @Value("${kafka.topic.action-logs}")
    private String topic;

    public ActionLogEventProducer(KafkaTemplate<String, ActionLogEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(ActionLogEvent event) {
        kafkaTemplate.send(topic, event);
        log.info("Действие пользователя добавлено в kafka: {} {}", event.getDeviceType(), event.getAction());
    }
}