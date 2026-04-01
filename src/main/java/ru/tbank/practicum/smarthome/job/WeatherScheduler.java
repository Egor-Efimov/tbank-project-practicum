package ru.tbank.practicum.smarthome.job;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class WeatherScheduler {
    private final WebClient webClient;

    public WeatherScheduler(WebClient webClient) {
        this.webClient = webClient;
    }

    @Scheduled(fixedDelay = 600000)
    public void fetchWeather() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("[" + timestamp + "] Запрос погоды...");

        try {
            Double lat = 51.5406;
            Double lon = 46.0086;
            String apiKey = "57631358db6f3b32ec59014d4079418e";

            String weatherData = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/data/2.5/weather")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("units", "metric")
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Погода: " + weatherData);

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}
