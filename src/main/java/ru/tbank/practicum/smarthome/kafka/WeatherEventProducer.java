package ru.tbank.practicum.smarthome.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.tbank.practicum.smarthome.event.WeatherEvent;

@Component
public class WeatherEventProducer {

    private static final Logger log = LoggerFactory.getLogger(WeatherEventProducer.class);

    private final KafkaTemplate<String, WeatherEvent> kafkaTemplate;

    @Value("${kafka.topic.weather-events}")
    private String topic;

    public WeatherEventProducer(KafkaTemplate<String, WeatherEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(WeatherEvent event) {
        kafkaTemplate.send(topic, event);
        log.info("Событие погоды отправлено в Kafka: {} {}", event.getCity(), event.getTemperature());
    }
}
