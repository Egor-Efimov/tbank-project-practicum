package ru.tbank.practicum.smarthome.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.practicum.smarthome.entity.*;
import ru.tbank.practicum.smarthome.repository.*;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class SmartHomeService {

    private final BatteryRepository batteryRepository;
    private final BlindsRepository blindsRepository;
    private final ScheduleRepository scheduleRepository;
    private final RoomRepository roomRepository;
    private final ActionLogRepository actionLogRepository;

    public SmartHomeService(BatteryRepository batteryRepository,
                            BlindsRepository blindsRepository,
                            ScheduleRepository scheduleRepository,
                            RoomRepository roomRepository,
                            ActionLogRepository actionLogRepository) {
        this.batteryRepository = batteryRepository;
        this.blindsRepository = blindsRepository;
        this.scheduleRepository = scheduleRepository;
        this.roomRepository = roomRepository;
        this.actionLogRepository = actionLogRepository;
    }

    public int setBatteryTemperature(String roomName, int temperature) {
        RoomEntity room = roomRepository.findByName(roomName)
                .orElseThrow(() -> new RuntimeException("Комната '" + roomName + "' не найдена"));

        BatteryEntity battery = batteryRepository.findByRoomId(room.getId())
                .orElseThrow(() -> new RuntimeException("Батарея в комнате '" + roomName + "' не найдена"));

        // Сохраняем старое значение для лога
        Integer oldValue = battery.getTemperature();

        // Обновляем температуру
        battery.setTemperature(temperature);
        battery.setLastUpdated(LocalDateTime.now());
        batteryRepository.save(battery);

        // Сохраняем лог действия
        ActionLogEntity log = new ActionLogEntity();
        log.setDeviceType("BATTERY");
        log.setAction("SET_TEMPERATURE");
        log.setOldValue(oldValue);
        log.setNewValue(temperature);
        log.setSource("USER");
        log.setRoom(room);
        actionLogRepository.save(log);

        System.out.println("Батарея в комнате " + roomName + " установлена на " + temperature + " градусов");
        return temperature;
    }

    public int getBatteryTemperature(String roomName) {
        RoomEntity room = roomRepository.findByName(roomName)
                .orElseThrow(() -> new RuntimeException("Комната '" + roomName + "' не найдена"));

        BatteryEntity battery = batteryRepository.findByRoomId(room.getId())
                .orElseThrow(() -> new RuntimeException("Батарея в комнате '" + roomName + "' не найдена"));

        int temp = battery.getTemperature();
        System.out.println("Запрошена температура в комнате " + roomName + ": " + temp + " градусов");
        return temp;
    }

    public int setBlindsPosition(String roomName, int position) {
        if (position < 0) position = 0;
        if (position > 100) position = 100;

        RoomEntity room = roomRepository.findByName(roomName)
                .orElseThrow(() -> new RuntimeException("Комната '" + roomName + "' не найдена"));

        BlindsEntity blinds = blindsRepository.findByRoomId(room.getId())
                .orElseThrow(() -> new RuntimeException("Жалюзи в комнате '" + roomName + "' не найдены"));

        Integer oldValue = blinds.getPosition();
        blinds.setPosition(position);
        blinds.setLastUpdated(LocalDateTime.now());
        blindsRepository.save(blinds);

        ActionLogEntity log = new ActionLogEntity();
        log.setDeviceType("BLINDS");
        log.setAction("SET_POSITION");
        log.setOldValue(oldValue);
        log.setNewValue(position);
        log.setSource("USER");
        log.setRoom(room);
        actionLogRepository.save(log);

        System.out.println("Жалюзи в комнате " + roomName + " установлены на " + position + "%");
        return position;
    }

    public int getBlindsPosition(String roomName) {
        RoomEntity room = roomRepository.findByName(roomName)
                .orElseThrow(() -> new RuntimeException("Комната '" + roomName + "' не найдена"));

        BlindsEntity blinds = blindsRepository.findByRoomId(room.getId())
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
        RoomEntity room = roomRepository.findByName(roomName)
                .orElseThrow(() -> new RuntimeException("Комната '" + roomName + "' не найдена"));

        ScheduleEntity schedule = new ScheduleEntity();
        schedule.setTime(time);
        schedule.setAction(action.toUpperCase());
        schedule.setEnabled(true);
        schedule.setRoom(room);

        ScheduleEntity saved = scheduleRepository.save(schedule);

        ActionLogEntity log = new ActionLogEntity();
        log.setDeviceType("SCHEDULE");
        log.setAction("CREATE");
        log.setSource("USER");
        log.setRoom(room);
        actionLogRepository.save(log);

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
