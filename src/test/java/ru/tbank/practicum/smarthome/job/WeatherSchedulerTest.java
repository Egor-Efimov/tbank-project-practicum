package ru.tbank.practicum.smarthome.job;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.tbank.practicum.smarthome.repository.WeatherLogRepository;
import ru.tbank.practicum.smarthome.service.dto.WeatherApiResponse;

import java.util.List;
import java.util.function.Function;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherSchedulerTest {

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @Mock
    private WeatherLogRepository weatherLogRepository;

    @InjectMocks
    private WeatherScheduler weatherScheduler;

    @Test
    void fetchWeather_shouldSaveWeatherToDatabase() {
        WeatherApiResponse response = createMockWeatherResponse();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(WeatherApiResponse.class)).thenReturn(Mono.just(response));

        weatherScheduler.fetchWeather();

        verify(weatherLogRepository).save(any());
    }

    private WeatherApiResponse createMockWeatherResponse() {
        WeatherApiResponse response = new WeatherApiResponse();

        WeatherApiResponse.Main main = new WeatherApiResponse.Main();
        main.setTemp(15.0);
        main.setFeelsLike(14.0);
        main.setHumidity(80);
        main.setPressure(1000);
        response.setMain(main);

        WeatherApiResponse.Weather weather = new WeatherApiResponse.Weather();
        weather.setDescription("cloudy");
        response.setWeather(List.of(weather));

        WeatherApiResponse.Wind wind = new WeatherApiResponse.Wind();
        wind.setSpeed(3.0);
        response.setWind(wind);
        response.setName("Saratov");

        return response;
    }
}