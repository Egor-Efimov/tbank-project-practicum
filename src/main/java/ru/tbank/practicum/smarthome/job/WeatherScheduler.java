package ru.tbank.practicum.smarthome.job;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.tbank.practicum.smarthome.entity.WeatherLogEntity;
import ru.tbank.practicum.smarthome.repository.WeatherLogRepository;
import ru.tbank.practicum.smarthome.service.dto.WeatherApiResponse;

@Component
public class WeatherScheduler {

    private final WebClient webClient;
    private final WeatherLogRepository weatherLogRepository;

    public WeatherScheduler(WebClient webClient, WeatherLogRepository weatherLogRepository) {
        this.webClient = webClient;
        this.weatherLogRepository = weatherLogRepository;
    }

    @Scheduled(fixedDelay = 600000)
    public void fetchWeather() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("[" + timestamp + "] Запрос погоды...");

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
                    .bodyToMono(WeatherApiResponse.class) // парсим сразу в объект
                    .block();

            // cоздаем сущность для БД и сохраняем, с логом в консоль
            WeatherLogEntity log = new WeatherLogEntity();
            log.setTimestamp(LocalDateTime.now());
            log.setCity(response.getName());
            log.setTemperature(response.getMain().getTemp());
            log.setFeelsLike(response.getMain().getFeelsLike());
            log.setDescription(response.getWeather().get(0).getDescription());
            log.setHumidity(response.getMain().getHumidity());
            log.setPressure(response.getMain().getPressure());
            log.setWindSpeed(response.getWind().getSpeed());

            weatherLogRepository.save(log);

            System.out.println("Сохранена погода для " + response.getName() + ": "
                    + response.getMain().getTemp() + "°C, "
                    + response.getWeather().get(0).getDescription());

        } catch (Exception e) {
            System.err.println("Ошибка при запросе или сохранении погоды: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
