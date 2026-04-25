package ru.tbank.practicum.smarthome.kafka;

import java.time.LocalDateTime;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.tbank.practicum.smarthome.entity.WeatherLogEntity;
import ru.tbank.practicum.smarthome.event.WeatherEvent;
import ru.tbank.practicum.smarthome.repository.WeatherLogRepository;

@Component
public class WeatherEventConsumer {

    private final WeatherLogRepository weatherLogRepository;

    public WeatherEventConsumer(WeatherLogRepository weatherLogRepository) {
        this.weatherLogRepository = weatherLogRepository;
    }

    @KafkaListener(topics = "weather-events", groupId = "smarthome-group")
    public void consume(WeatherEvent event) {
        WeatherLogEntity log = new WeatherLogEntity();
        log.setTimestamp(LocalDateTime.parse(event.getTimestamp()));
        log.setCity(event.getCity());
        log.setTemperature(event.getTemperature());
        log.setFeelsLike(event.getFeelsLike());
        log.setDescription(event.getDescription());
        log.setHumidity(event.getHumidity());
        log.setPressure(event.getPressure());
        log.setWindSpeed(event.getWindSpeed());

        weatherLogRepository.save(log);
        System.out.println("Сохранена погода из Kafka: " + event.getCity() + " " + event.getTemperature());
    }
}
