package ru.tbank.practicum.smarthome.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.practicum.smarthome.entity.ActionLogEntity;
import ru.tbank.practicum.smarthome.entity.BatteryEntity;
import ru.tbank.practicum.smarthome.entity.BlindsEntity;
import ru.tbank.practicum.smarthome.entity.RoomEntity;
import ru.tbank.practicum.smarthome.entity.ScheduleEntity;
import ru.tbank.practicum.smarthome.repository.ActionLogRepository;
import ru.tbank.practicum.smarthome.repository.BatteryRepository;
import ru.tbank.practicum.smarthome.repository.BlindsRepository;
import ru.tbank.practicum.smarthome.repository.RoomRepository;
import ru.tbank.practicum.smarthome.repository.ScheduleRepository;

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
    private static final Logger log = LoggerFactory.getLogger(SmartHomeService.class);

    public SmartHomeService(
            BatteryRepository batteryRepository,
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
        RoomEntity room = roomRepository
                .findByName(roomName)
                .orElseThrow(() -> new RuntimeException("Комната '" + roomName + "' не найдена"));

        BatteryEntity battery = batteryRepository
                .findByRoomId(room.getId())
                .orElseThrow(() -> new RuntimeException("Батарея в комнате '" + roomName + "' не найдена"));

        Integer oldValue = battery.getTemperature();
        battery.setTemperature(temperature);
        battery.setLastUpdated(LocalDateTime.now());
        batteryRepository.save(battery);

        ActionLogEntity actionLog = new ActionLogEntity();
        actionLog.setDeviceType("BATTERY");
        actionLog.setAction("SET_TEMPERATURE");
        actionLog.setOldValue(oldValue);
        actionLog.setNewValue(temperature);
        actionLog.setSource("USER");
        actionLog.setRoom(room);
        actionLogRepository.save(actionLog);

        log.info("Батарея в комнате {} установлена на {} градусов (было {})", roomName, temperature, oldValue);
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
        log.info("Запрошена температура в комнате {}: {} градусов", roomName, temp);
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

        ActionLogEntity actionLog = new ActionLogEntity();
        actionLog.setDeviceType("BLINDS");
        actionLog.setAction("SET_POSITION");
        actionLog.setOldValue(oldValue);
        actionLog.setNewValue(position);
        actionLog.setSource("USER");
        actionLog.setRoom(room);
        actionLogRepository.save(actionLog);

        log.info("Жалюзи в комнате {} установлены на {}% (было {}%)", roomName, position, oldValue);
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
        log.info("Запрошена позиция жалюзи в комнате {}: {}%", roomName, pos);
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

        ActionLogEntity actionLog = new ActionLogEntity();
        actionLog.setDeviceType("SCHEDULE");
        actionLog.setAction("CREATE");
        actionLog.setSource("USER");
        actionLog.setRoom(room);
        actionLogRepository.save(actionLog);

        log.info("Добавлено расписание: {} - {} для комнаты {}", time, action, roomName);
        return saved;
    }

    public List<ScheduleEntity> getAllSchedules() {
        log.info("Запрошен список всех расписаний");
        return scheduleRepository.findAll();
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.findById(id).ifPresent(schedule -> {
            scheduleRepository.delete(schedule);
            log.info("Удалено расписание с id: {}", id);
        });
    }
}