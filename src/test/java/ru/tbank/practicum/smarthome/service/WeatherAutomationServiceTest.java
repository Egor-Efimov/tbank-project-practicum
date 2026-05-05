package ru.tbank.practicum.smarthome.service;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.practicum.smarthome.entity.RoomEntity;
import ru.tbank.practicum.smarthome.event.WeatherEvent;

@ExtendWith(MockitoExtension.class)
class WeatherAutomationServiceTest {

    @Mock
    private SmartHomeService smartHomeService;

    @InjectMocks
    private WeatherAutomationService weatherAutomationService;

    @Test
    void applyRules_whenColdAndSunny_thenIncreaseBatteryAndCloseBlinds() {
        RoomEntity room = new RoomEntity();
        room.setName("kitchen");
        when(smartHomeService.getAllRooms()).thenReturn(List.of(room));

        WeatherEvent event = new WeatherEvent();
        event.setTemperature(5.0);
        event.setDescription("clear sky");

        weatherAutomationService.applyRules(event);

        verify(smartHomeService).setBatteryTemperature("kitchen", 24, SmartHomeService.SOURCE_WEATHER_RULE);
        verify(smartHomeService).closeBlinds("kitchen", SmartHomeService.SOURCE_WEATHER_RULE);
    }
}
