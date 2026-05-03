package ru.tbank.practicum.smarthome.kafka;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.tbank.practicum.smarthome.entity.WeatherLogEntity;
import ru.tbank.practicum.smarthome.event.WeatherEvent;
import ru.tbank.practicum.smarthome.mapper.WeatherMapper;
import ru.tbank.practicum.smarthome.repository.WeatherLogRepository;

@Component
public class WeatherEventConsumer {

    private final WeatherLogRepository weatherLogRepository;
    private final WeatherMapper weatherMapper;

    public WeatherEventConsumer(WeatherLogRepository weatherLogRepository, WeatherMapper weatherMapper) {
        this.weatherLogRepository = weatherLogRepository;
        this.weatherMapper = weatherMapper;
    }

    @KafkaListener(topics = "${kafka.topic.weather-events}", groupId = "smarthome-group")
    public void consume(WeatherEvent event) {
        WeatherLogEntity log = weatherMapper.toEntity(event);
        weatherLogRepository.save(log);
        System.out.println("Сохранена погода из Kafka: " + event.getCity() + " " + event.getTemperature());
    }
}
