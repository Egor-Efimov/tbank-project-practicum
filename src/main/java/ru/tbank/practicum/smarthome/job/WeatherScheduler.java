package ru.tbank.practicum.smarthome.job;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import ru.tbank.practicum.smarthome.event.WeatherEvent;
import ru.tbank.practicum.smarthome.kafka.WeatherEventProducer;

@Component
public class WeatherScheduler {

    private final WebClient webClient;
    private final WeatherEventProducer weatherEventProducer;

    public WeatherScheduler(WebClient webClient, WeatherEventProducer weatherEventProducer) {
        this.webClient = webClient;
        this.weatherEventProducer = weatherEventProducer;
    }

    @Scheduled(fixedDelay = 600000)
    public void fetchWeather() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("[" + timestamp + "] Запрос погоды...");

        try {
            Double lat = 51.5751;
            Double lon = 46.0133;
            String apiKey = "57631358db6f3b32ec59014d4079418e";

            var response = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/data/2.5/weather")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("units", "metric")
                            .queryParam("appid", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(ru.tbank.practicum.smarthome.service.dto.WeatherApiResponse.class)
                    .block();

            WeatherEvent event = new WeatherEvent(
                    UUID.randomUUID().toString(),
                    LocalDateTime.now().toString(),
                    response.getName(),
                    response.getMain().getTemp(),
                    response.getMain().getFeelsLike(),
                    response.getWeather().get(0).getDescription(),
                    response.getMain().getHumidity(),
                    response.getMain().getPressure(),
                    response.getWind().getSpeed());

            weatherEventProducer.send(event);

            System.out.println("Погода для " + response.getName() + " отправлена в Kafka: "
                    + response.getMain().getTemp() + "°C, "
                    + response.getWeather().get(0).getDescription());

        } catch (Exception e) {
            System.err.println("Ошибка при запросе погоды: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
