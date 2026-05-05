package ru.tbank.practicum.smarthome.service;

import java.util.List;
import java.util.Locale;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.tbank.practicum.smarthome.entity.RoomEntity;
import ru.tbank.practicum.smarthome.event.WeatherEvent;

@Service
public class WeatherAutomationService {

    private static final Logger log = LoggerFactory.getLogger(WeatherAutomationService.class);
    private static final int COLD_THRESHOLD = 10;
    private static final int HOT_THRESHOLD = 25;
    private static final int COLD_WEATHER_TARGET_TEMP = 24;
    private static final int HOT_WEATHER_TARGET_TEMP = 0;

    private final SmartHomeService smartHomeService;

    public WeatherAutomationService(SmartHomeService smartHomeService) {
        this.smartHomeService = smartHomeService;
    }

    public void applyRules(WeatherEvent event) {
        if (event == null || event.getTemperature() == null) {
            return;
        }

        List<RoomEntity> rooms = smartHomeService.getAllRooms();
        if (rooms.isEmpty()) {
            return;
        }

        double temperature = event.getTemperature();
        String description =
                event.getDescription() == null ? "" : event.getDescription().toLowerCase(Locale.ROOT);
        boolean sunny = description.contains("clear") || description.contains("sun") || description.contains("ясно");

        for (RoomEntity room : rooms) {
            String roomName = room.getName();

            if (temperature < COLD_THRESHOLD) {
                smartHomeService.setBatteryTemperature(
                        roomName, COLD_WEATHER_TARGET_TEMP, SmartHomeService.SOURCE_WEATHER_RULE);
            } else if (temperature > HOT_THRESHOLD) {
                smartHomeService.setBatteryTemperature(
                        roomName, HOT_WEATHER_TARGET_TEMP, SmartHomeService.SOURCE_WEATHER_RULE);
            }

            if (sunny) {
                smartHomeService.closeBlinds(roomName, SmartHomeService.SOURCE_WEATHER_RULE);
            } else if (temperature <= HOT_THRESHOLD) {
                smartHomeService.openBlinds(roomName, SmartHomeService.SOURCE_WEATHER_RULE);
            }
        }

        log.info(
                "Применены погодные правила: temp={}, desc='{}', rooms={}",
                temperature,
                event.getDescription(),
                rooms.size());
    }
}
