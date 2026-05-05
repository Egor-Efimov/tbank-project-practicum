package ru.tbank.practicum.smarthome.mapper;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Component;
import ru.tbank.practicum.smarthome.entity.WeatherLogEntity;
import ru.tbank.practicum.smarthome.event.WeatherEvent;
import ru.tbank.practicum.smarthome.service.dto.WeatherApiResponse;

@Component
public class WeatherMapper {

    public WeatherEvent toEvent(WeatherApiResponse response) {
        return new WeatherEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now().toString(),
                response.getName(),
                response.getMain().getTemp(),
                response.getMain().getFeelsLike(),
                response.getWeather().get(0).getDescription(),
                response.getMain().getHumidity(),
                response.getMain().getPressure(),
                response.getWind().getSpeed());
    }

    public WeatherLogEntity toEntity(WeatherEvent event) {
        WeatherLogEntity log = new WeatherLogEntity();
        log.setTimestamp(LocalDateTime.parse(event.getTimestamp()));
        log.setCity(event.getCity());
        log.setTemperature(event.getTemperature());
        log.setFeelsLike(event.getFeelsLike());
        log.setDescription(event.getDescription());
        log.setHumidity(event.getHumidity());
        log.setPressure(event.getPressure());
        log.setWindSpeed(event.getWindSpeed());
        return log;
    }
}
