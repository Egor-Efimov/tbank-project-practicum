package ru.tbank.practicum.smarthome.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class SmartHomeService {
    private Map<String, Integer> batteryStorage = new HashMap<>();

    public void setBatteryTemperature(String room, int temperature) {
        batteryStorage.put(room, temperature);
        System.out.println("Батарея в комнате " + room + " установлена на " + temperature + " градусов");
    }

    public int getBatteryTemperature(String room) {
        int temp = batteryStorage.getOrDefault(room, 25);
        System.out.println("Запрошена температура в комнате" + room + ", которая равна " + temp + " градусов");
        return temp;
    }
}
