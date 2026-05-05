package ru.tbank.practicum.smarthome.job;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.tbank.practicum.smarthome.event.WeatherEvent;
import ru.tbank.practicum.smarthome.kafka.WeatherEventProducer;
import ru.tbank.practicum.smarthome.mapper.WeatherMapper;
import ru.tbank.practicum.smarthome.service.dto.WeatherApiResponse;

@Component
public class WeatherScheduler {

    private static final Logger log = LoggerFactory.getLogger(WeatherScheduler.class);

    private final WebClient webClient;
    private final WeatherEventProducer weatherEventProducer;
    private final WeatherMapper weatherMapper;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.lat}")
    private Double lat;

    @Value("${weather.lon}")
    private Double lon;

    public WeatherScheduler(
            WebClient webClient, WeatherEventProducer weatherEventProducer, WeatherMapper weatherMapper) {
        this.webClient = webClient;
        this.weatherEventProducer = weatherEventProducer;
        this.weatherMapper = weatherMapper;
    }

    @Scheduled(fixedDelay = 600000)
    public void fetchWeather() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        log.info("[{}] Запрос погоды...", timestamp);

        try {
            WeatherApiResponse response = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/data/2.5/weather")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("units", "metric")
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(WeatherApiResponse.class)
                    .block();

            WeatherEvent event = weatherMapper.toEvent(response);
            weatherEventProducer.send(event);

            log.info(
                    "Погода для {} отправлена в Kafka: {}°C, {}",
                    response.getName(),
                    response.getMain().getTemp(),
                    response.getWeather().get(0).getDescription());

        } catch (Exception e) {
            log.error("Ошибка при запросе погоды: {}", e.getMessage(), e);
        }
    }
}
