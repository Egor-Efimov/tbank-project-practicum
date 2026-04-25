package ru.tbank.practicum.smarthome.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.tbank.practicum.smarthome.event.ActionLogEvent;

@Component
public class ActionLogEventProducer {

    private final KafkaTemplate<String, ActionLogEvent> kafkaTemplate;
    private static final String TOPIC = "action-logs";

    public ActionLogEventProducer(KafkaTemplate<String, ActionLogEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(ActionLogEvent event) {
        kafkaTemplate.send(TOPIC, event);
        System.out.println(
                "Действие пользователя добавлено в kafka: " + event.getDeviceType() + " " + event.getAction());
    }
}
