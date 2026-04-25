package ru.tbank.practicum.smarthome.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherEvent {
    private String eventId;
    private String timestamp;
    private String city;
    private Double temperature;
    private Double feelsLike;
    private String description;
    private Integer humidity;
    private Integer pressure;
    private Double windSpeed;
}
