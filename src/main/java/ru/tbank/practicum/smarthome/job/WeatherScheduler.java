package ru.tbank.practicum.smarthome.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.tbank.practicum.smarthome.entity.WeatherLogEntity;
import ru.tbank.practicum.smarthome.repository.WeatherLogRepository;
import ru.tbank.practicum.smarthome.service.dto.WeatherApiResponse;

import java.time.LocalDateTime;

@Component
public class WeatherScheduler {

    private static final Logger log = LoggerFactory.getLogger(WeatherScheduler.class);

    private final WebClient webClient;
    private final WeatherLogRepository weatherLogRepository;

    public WeatherScheduler(WebClient webClient, WeatherLogRepository weatherLogRepository) {
        this.webClient = webClient;
        this.weatherLogRepository = weatherLogRepository;
    }

    @Scheduled(fixedDelay = 600000)
    public void fetchWeather() {
        log.info("Запрос погоды...");

        try {
            Double lat = 51.5751;
            Double lon = 46.0133;
            String apiKey = "57631358db6f3b32ec59014d4079418e";

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

            WeatherLogEntity weatherLog = new WeatherLogEntity();
            weatherLog.setTimestamp(LocalDateTime.now());
            weatherLog.setCity(response.getName());
            weatherLog.setTemperature(response.getMain().getTemp());
            weatherLog.setFeelsLike(response.getMain().getFeelsLike());
            weatherLog.setDescription(response.getWeather().get(0).getDescription());
            weatherLog.setHumidity(response.getMain().getHumidity());
            weatherLog.setPressure(response.getMain().getPressure());
            weatherLog.setWindSpeed(response.getWind().getSpeed());

            weatherLogRepository.save(weatherLog);

            log.info("Сохранена погода для {}: {}°C, {}",
                    response.getName(),
                    response.getMain().getTemp(),
                    response.getWeather().get(0).getDescription());

        } catch (Exception e) {
            log.error("Ошибка при запросе или сохранении погоды: {}", e.getMessage(), e);
        }
    }
}