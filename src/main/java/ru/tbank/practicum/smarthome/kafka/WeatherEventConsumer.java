package ru.tbank.practicum.smarthome.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.tbank.practicum.smarthome.entity.WeatherLogEntity;
import ru.tbank.practicum.smarthome.event.WeatherEvent;
import ru.tbank.practicum.smarthome.mapper.WeatherMapper;
import ru.tbank.practicum.smarthome.repository.WeatherLogRepository;

@Component
public class WeatherEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(WeatherEventConsumer.class);

    private final WeatherLogRepository weatherLogRepository;
    private final WeatherMapper weatherMapper;

    public WeatherEventConsumer(WeatherLogRepository weatherLogRepository, WeatherMapper weatherMapper) {
        this.weatherLogRepository = weatherLogRepository;
        this.weatherMapper = weatherMapper;
    }

    @KafkaListener(topics = "${kafka.topic.weather-events}", groupId = "smarthome-group")
    public void consume(WeatherEvent event) {
        WeatherLogEntity logEntity = weatherMapper.toEntity(event);
        weatherLogRepository.save(logEntity);
        log.info("Сохранена погода из Kafka: {} {}", event.getCity(), event.getTemperature());
    }
}