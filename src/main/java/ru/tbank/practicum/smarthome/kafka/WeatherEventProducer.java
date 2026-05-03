package ru.tbank.practicum.smarthome.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.tbank.practicum.smarthome.event.WeatherEvent;

@Component
public class WeatherEventProducer {

    private final KafkaTemplate<String, WeatherEvent> kafkaTemplate;

    @Value("${kafka.topic.weather-events}")
    private String topic;

    public WeatherEventProducer(KafkaTemplate<String, WeatherEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(WeatherEvent event) {
        kafkaTemplate.send(topic, event);
        System.out.println("Событие погоды отправлено в Kafka: " + event.getCity() + " " + event.getTemperature());
    }
}
