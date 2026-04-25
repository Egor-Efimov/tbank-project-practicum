package ru.tbank.practicum.smarthome.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.practicum.smarthome.entity.*;
import ru.tbank.practicum.smarthome.event.ActionLogEvent;
import ru.tbank.practicum.smarthome.kafka.ActionLogEventProducer;
import ru.tbank.practicum.smarthome.repository.*;

@Service
@Transactional
public class SmartHomeService {

    private final BatteryRepository batteryRepository;
    private final BlindsRepository blindsRepository;
    private final ScheduleRepository scheduleRepository;
    private final RoomRepository roomRepository;
    private final ActionLogRepository actionLogRepository;
    private final ActionLogEventProducer actionLogEventProducer;

    public SmartHomeService(
            BatteryRepository batteryRepository,
            BlindsRepository blindsRepository,
            ScheduleRepository scheduleRepository,
            RoomRepository roomRepository,
            ActionLogRepository actionLogRepository,
            ActionLogEventProducer actionLogEventProducer) {
        this.batteryRepository = batteryRepository;
        this.blindsRepository = blindsRepository;
        this.scheduleRepository = scheduleRepository;
        this.roomRepository = roomRepository;
        this.actionLogRepository = actionLogRepository;
        this.actionLogEventProducer = actionLogEventProducer;
    }

    public int setBatteryTemperature(String roomName, int temperature) {
        RoomEntity room = roomRepository
                .findByName(roomName)
                .orElseThrow(() -> new RuntimeException("Комната '" + roomName + "' не найдена"));

        BatteryEntity battery = batteryRepository
                .findByRoomId(room.getId())
                .orElseThrow(() -> new RuntimeException("Батарея в комнате '" + roomName + "' не найдена"));

        // Сохраняем старое значение для лога
        Integer oldValue = battery.getTemperature();

        // Обновляем температуру
        battery.setTemperature(temperature);
        battery.setLastUpdated(LocalDateTime.now());
        batteryRepository.save(battery);

        // Отправляем событие в Kafka
        ActionLogEvent event = new ActionLogEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now().toString(),
                "BATTERY",
                "SET_TEMPERATURE",
                roomName,
                oldValue,
                temperature,
                "USER");
        actionLogEventProducer.send(event);

        System.out.println("Батарея в комнате " + roomName + " установлена на " + temperature + " градусов");
        return temperature;
    }

    public int getBatteryTemperature(String roomName) {
        RoomEntity room = roomRepository
                .findByName(roomName)
                .orElseThrow(() -> new RuntimeException("Комната '" + roomName + "' не найдена"));

        BatteryEntity battery = batteryRepository
                .findByRoomId(room.getId())
                .orElseThrow(() -> new RuntimeException("Батарея в комнате '" + roomName + "' не найдена"));

        int temp = battery.getTemperature();
        System.out.println("Запрошена температура в комнате " + roomName + ": " + temp + " градусов");
        return temp;
    }

    public int setBlindsPosition(String roomName, int position) {
        if (position < 0) position = 0;
        if (position > 100) position = 100;

        RoomEntity room = roomRepository
                .findByName(roomName)
                .orElseThrow(() -> new RuntimeException("Комната '" + roomName + "' не найдена"));

        BlindsEntity blinds = blindsRepository
                .findByRoomId(room.getId())
                .orElseThrow(() -> new RuntimeException("Жалюзи в комнате '" + roomName + "' не найдены"));

        Integer oldValue = blinds.getPosition();
        blinds.setPosition(position);
        blinds.setLastUpdated(LocalDateTime.now());
        blindsRepository.save(blinds);

        ActionLogEvent event = new ActionLogEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now().toString(),
                "BLINDS",
                "SET_POSITION",
                roomName,
                oldValue,
                position,
                "USER");
        actionLogEventProducer.send(event);

        System.out.println("Жалюзи в комнате " + roomName + " установлены на " + position + "%");
        return position;
    }

    public int getBlindsPosition(String roomName) {
        RoomEntity room = roomRepository
                .findByName(roomName)
                .orElseThrow(() -> new RuntimeException("Комната '" + roomName + "' не найдена"));

        BlindsEntity blinds = blindsRepository
                .findByRoomId(room.getId())
                .orElseThrow(() -> new RuntimeException("Жалюзи в комнате '" + roomName + "' не найдены"));

        int pos = blinds.getPosition();
        System.out.println("Запрошена позиция жалюзи в комнате " + roomName + ": " + pos + "%");
        return pos;
    }

    public void openBlinds(String roomName) {
        setBlindsPosition(roomName, 100);
    }

    public void closeBlinds(String roomName) {
        setBlindsPosition(roomName, 0);
    }

    public ScheduleEntity addSchedule(String time, String roomName, String action) {
        RoomEntity room = roomRepository
                .findByName(roomName)
                .orElseThrow(() -> new RuntimeException("Комната '" + roomName + "' не найдена"));

        ScheduleEntity schedule = new ScheduleEntity();
        schedule.setTime(time);
        schedule.setAction(action.toUpperCase());
        schedule.setEnabled(true);
        schedule.setRoom(room);

        ScheduleEntity saved = scheduleRepository.save(schedule);

        ActionLogEvent event = new ActionLogEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now().toString(),
                "SCHEDULE",
                "CREATE",
                roomName,
                null,
                null,
                "USER");
        actionLogEventProducer.send(event);

        System.out.println("Добавлено расписание: " + time + " - " + action + " для " + roomName);
        return saved;
    }

    public List<ScheduleEntity> getAllSchedules() {
        System.out.println("Запрошен список всех расписаний");
        return scheduleRepository.findAll();
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.findById(id).ifPresent(schedule -> {
            scheduleRepository.delete(schedule);
            System.out.println("Удалено расписание с id: " + id);
        });
    }
}
