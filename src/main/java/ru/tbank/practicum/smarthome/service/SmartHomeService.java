package ru.tbank.practicum.smarthome.service;

import java.util.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

@Service
public class SmartHomeService {

    private Map<String, Integer> batteryStorage = new HashMap<>();

    private Map<String, Integer> blindsStorage = new HashMap<>();

    private List<ScheduleTask> scheduleStorage = new ArrayList<>();

    private AtomicLong scheduleIdCounter = new AtomicLong(1);

    public int setBatteryTemperature(String room, int temperature) {
        batteryStorage.put(room, temperature);
        System.out.println("Батарея в комнате " + room + " установлена на " + temperature + " градусов");
        return temperature;
    }

    public int getBatteryTemperature(String room) {
        int temp = batteryStorage.getOrDefault(room, 25);
        System.out.println("Запрошена температура в комнате " + room + ", которая равна " + temp + " градусов");
        return temp;
    }

    public int setBlindsPosition(String room, int position) {
        if (position < 0) position = 0;
        if (position > 100) position = 100;

        blindsStorage.put(room, position);
        System.out.println("Жалюзи в комнате " + room + " установлены на " + position + "%");
        return position;
    }

    public int getBlindsPosition(String room) {
        int pos = blindsStorage.getOrDefault(room, 50);
        System.out.println("Запрошена позиция жалюзи в комнате " + room + ": " + pos + "%");
        return pos;
    }

    public void openBlinds(String room) {
        setBlindsPosition(room, 100);
    }

    public void closeBlinds(String room) {
        setBlindsPosition(room, 0);
    }

    public ScheduleTask addSchedule(String time, String room, String action) {
        ScheduleTask task =
                new ScheduleTask(scheduleIdCounter.getAndIncrement(), time, room, action.toUpperCase(), true);
        scheduleStorage.add(task);
        System.out.println("Добавлено расписание: " + task);
        return task;
    }

    public List<ScheduleTask> getAllSchedules() {
        System.out.println("Запрошен список всех расписаний (всего: " + scheduleStorage.size() + ")");
        return new ArrayList<>(scheduleStorage);
    }

    public void deleteSchedule(Long id) {
        boolean removed = scheduleStorage.removeIf(task -> task.getId().equals(id));
        if (removed) {
            System.out.println("Удалено расписание с id: " + id);
        } else {
            System.out.println("Расписание с id " + id + " не найдено");
        }
    }
}
