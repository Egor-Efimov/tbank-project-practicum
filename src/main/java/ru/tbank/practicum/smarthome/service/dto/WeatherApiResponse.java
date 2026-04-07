package ru.tbank.practicum.smarthome.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class WeatherApiResponse {

    private Coord coord;
    private List<Weather> weather;
    private Main main;
    private Wind wind;
    private String name;

    @Data
    public static class Coord {
        private double lon;
        private double lat;
    }

    @Data
    public static class Weather {
        private int id;
        private String main;
        private String description;
        private String icon;
    }

    @Data
    public static class Main {
        private double temp;
        @JsonProperty("feels_like")
        private double feelsLike;
        private int pressure;
        private int humidity;
    }

    @Data
    public static class Wind {
        private double speed;
    }
}