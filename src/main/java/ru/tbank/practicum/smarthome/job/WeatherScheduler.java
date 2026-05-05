package ru.tbank.practicum.smarthome.job;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    private final WebClient webClient;
    private final WeatherEventProducer weatherEventProducer;
    private final WeatherMapper weatherMapper;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.lat}")
    private Double lat;

    @Value("${weather.lon}")
    private Double lon;

    @Value("${weather.interval}")
    private long interval;

    public WeatherScheduler(
            WebClient webClient, WeatherEventProducer weatherEventProducer, WeatherMapper weatherMapper) {
        this.webClient = webClient;
        this.weatherEventProducer = weatherEventProducer;
        this.weatherMapper = weatherMapper;
    }

    @Scheduled(fixedDelayString = "${weather.interval}")
    public void fetchWeather() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("[" + timestamp + "] Запрос погоды...");

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

            System.out.println("Погода для " + response.getName() + " отправлена в Kafka: "
                    + response.getMain().getTemp() + "°C, "
                    + response.getWeather().get(0).getDescription());

        } catch (Exception e) {
            System.err.println("Ошибка при запросе погоды: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
