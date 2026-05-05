package ru.tbank.practicum.smarthome.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.tbank.practicum.smarthome.entity.WeatherLogEntity;
import ru.tbank.practicum.smarthome.event.WeatherEvent;
import ru.tbank.practicum.smarthome.mapper.WeatherMapper;
import ru.tbank.practicum.smarthome.repository.WeatherLogRepository;
import ru.tbank.practicum.smarthome.service.WeatherAutomationService;

@Component
public class WeatherEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(WeatherEventConsumer.class);

    private final WeatherLogRepository weatherLogRepository;
    private final WeatherMapper weatherMapper;
    private final WeatherAutomationService weatherAutomationService;

    public WeatherEventConsumer(
            WeatherLogRepository weatherLogRepository,
            WeatherMapper weatherMapper,
            WeatherAutomationService weatherAutomationService) {
        this.weatherLogRepository = weatherLogRepository;
        this.weatherMapper = weatherMapper;
        this.weatherAutomationService = weatherAutomationService;
    }

    @KafkaListener(topics = "${kafka.topic.weather-events}", groupId = "smarthome-group")
    public void consume(WeatherEvent event) {
        WeatherLogEntity logEntity = weatherMapper.toEntity(event);
        weatherLogRepository.save(logEntity);
        weatherAutomationService.applyRules(event);
        log.info("Сохранена погода из Kafka: {} {}", event.getCity(), event.getTemperature());
    }
}
