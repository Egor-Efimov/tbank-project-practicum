package ru.tbank.practicum.smarthome.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.tbank.practicum.smarthome.event.WeatherEvent;

@Component
public class WeatherEventProducer {

    private final KafkaTemplate<String, WeatherEvent> kafkaTemplate;
    private static final String TOPIC = "weather-events";

    public WeatherEventProducer(KafkaTemplate<String, WeatherEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(WeatherEvent event) {
        kafkaTemplate.send(TOPIC, event);
        System.out.println("Событие погоды отправлено в Kafka: " + event.getCity() + " " + event.getTemperature());
    }
}
