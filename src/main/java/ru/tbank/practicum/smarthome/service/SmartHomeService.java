package ru.tbank.practicum.smarthome.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tbank.practicum.smarthome.entity.BatteryEntity;
import ru.tbank.practicum.smarthome.entity.BlindsEntity;
import ru.tbank.practicum.smarthome.entity.RoomEntity;
import ru.tbank.practicum.smarthome.entity.ScheduleActionType;
import ru.tbank.practicum.smarthome.entity.ScheduleEntity;
import ru.tbank.practicum.smarthome.event.ActionLogEvent;
import ru.tbank.practicum.smarthome.exception.NotFoundException;
import ru.tbank.practicum.smarthome.exception.ValidationException;
import ru.tbank.practicum.smarthome.kafka.ActionLogEventProducer;
import ru.tbank.practicum.smarthome.repository.ActionLogRepository;
import ru.tbank.practicum.smarthome.repository.BatteryRepository;
import ru.tbank.practicum.smarthome.repository.BlindsRepository;
import ru.tbank.practicum.smarthome.repository.RoomRepository;
import ru.tbank.practicum.smarthome.repository.ScheduleRepository;

@Service
@Transactional
public class SmartHomeService {

    private static final Logger log = LoggerFactory.getLogger(SmartHomeService.class);
    public static final String SOURCE_USER = "USER";
    public static final String SOURCE_SCHEDULE = "SCHEDULE";
    public static final String SOURCE_WEATHER_RULE = "WEATHER_RULE";

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
        return setBatteryTemperature(roomName, temperature, SOURCE_USER);
    }

    public int setBatteryTemperature(String roomName, int temperature, String source) {
        RoomEntity room = roomRepository
                .findByName(roomName)
                .orElseThrow(() -> new NotFoundException("Комната '" + roomName + "' не найдена"));

        BatteryEntity battery = batteryRepository
                .findByRoomId(room.getId())
                .orElseThrow(() -> new NotFoundException("Батарея в комнате '" + roomName + "' не найдена"));

        Integer oldValue = battery.getTemperature();
        if (oldValue != null && oldValue == temperature) {
            log.info("Батарея в комнате {} уже выставлена на {} градусов, действие пропущено", roomName, temperature);
            return temperature;
        }

        battery.setTemperature(temperature);
        battery.setLastUpdated(LocalDateTime.now());
        batteryRepository.save(battery);

        ActionLogEvent event = new ActionLogEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now().toString(),
                "BATTERY",
                "SET_TEMPERATURE",
                roomName,
                oldValue,
                temperature,
                source);
        actionLogEventProducer.send(event);

        log.info(
                "Батарея в комнате {} установлена на {} градусов (было {}, source={})",
                roomName,
                temperature,
                oldValue,
                source);
        return temperature;
    }

    public int getBatteryTemperature(String roomName) {
        RoomEntity room = roomRepository
                .findByName(roomName)
                .orElseThrow(() -> new NotFoundException("Комната '" + roomName + "' не найдена"));

        BatteryEntity battery = batteryRepository
                .findByRoomId(room.getId())
                .orElseThrow(() -> new NotFoundException("Батарея в комнате '" + roomName + "' не найдена"));

        int temp = battery.getTemperature();
        log.info("Запрошена температура в комнате {}: {} градусов", roomName, temp);
        return temp;
    }

    public int setBlindsPosition(String roomName, int position) {
        return setBlindsPosition(roomName, position, SOURCE_USER);
    }

    public int setBlindsPosition(String roomName, int position, String source) {
        if (position < 0) position = 0;
        if (position > 100) position = 100;

        RoomEntity room = roomRepository
                .findByName(roomName)
                .orElseThrow(() -> new NotFoundException("Комната '" + roomName + "' не найдена"));

        BlindsEntity blinds = blindsRepository
                .findByRoomId(room.getId())
                .orElseThrow(() -> new NotFoundException("Жалюзи в комнате '" + roomName + "' не найдены"));

        Integer oldValue = blinds.getPosition();
        if (oldValue != null && oldValue == position) {
            log.info("Жалюзи в комнате {} уже выставлены на {}%, действие пропущено", roomName, position);
            return position;
        }

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
                source);
        actionLogEventProducer.send(event);

        log.info("Жалюзи в комнате {} установлены на {}% (было {}%, source={})", roomName, position, oldValue, source);
        return position;
    }

    public int getBlindsPosition(String roomName) {
        RoomEntity room = roomRepository
                .findByName(roomName)
                .orElseThrow(() -> new NotFoundException("Комната '" + roomName + "' не найдена"));

        BlindsEntity blinds = blindsRepository
                .findByRoomId(room.getId())
                .orElseThrow(() -> new NotFoundException("Жалюзи в комнате '" + roomName + "' не найдены"));

        int pos = blinds.getPosition();
        log.info("Запрошена позиция жалюзи в комнате {}: {}%", roomName, pos);
        return pos;
    }

    public void openBlinds(String roomName) {
        openBlinds(roomName, SOURCE_USER);
    }

    public void openBlinds(String roomName, String source) {
        setBlindsPosition(roomName, 100, source);
    }

    public void closeBlinds(String roomName) {
        closeBlinds(roomName, SOURCE_USER);
    }

    public void closeBlinds(String roomName, String source) {
        setBlindsPosition(roomName, 0, source);
    }

    public ScheduleEntity addSchedule(String time, String roomName, String action) {
        RoomEntity room = roomRepository
                .findByName(roomName)
                .orElseThrow(() -> new NotFoundException("Комната '" + roomName + "' не найдена"));

        ScheduleEntity schedule = new ScheduleEntity();
        schedule.setTime(time);
        schedule.setActionType(ScheduleActionType.valueOf(action.toUpperCase()));
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

        log.info("Добавлено расписание: {} - {} для комнаты {}", time, action, roomName);
        return saved;
    }

    public ScheduleEntity addSchedule(
            String time, String roomName, ScheduleActionType actionType, Integer targetValue, Boolean enabled) {
        RoomEntity room = roomRepository
                .findByName(roomName)
                .orElseThrow(() -> new NotFoundException("Комната '" + roomName + "' не найдена"));

        if (actionType == ScheduleActionType.SET_BATTERY_TEMPERATURE && targetValue == null) {
            throw new ValidationException("Для действия SET_BATTERY_TEMPERATURE требуется targetValue");
        }

        ScheduleEntity schedule = new ScheduleEntity();
        schedule.setTime(time);
        schedule.setActionType(actionType);
        schedule.setTargetValue(targetValue);
        schedule.setEnabled(enabled != null ? enabled : true);
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

        log.info("Добавлено расписание: {} - {} (target={}) для комнаты {}", time, actionType, targetValue, roomName);
        return saved;
    }

    public List<ScheduleEntity> getAllSchedules() {
        log.info("Запрошен список всех расписаний");
        return scheduleRepository.findAll();
    }

    public boolean executeScheduleAction(ScheduleEntity schedule, LocalDateTime executionTime) {
        String roomName = schedule.getRoom().getName();
        switch (schedule.getActionType()) {
            case OPEN_BLINDS -> openBlinds(roomName, SOURCE_SCHEDULE);
            case CLOSE_BLINDS -> closeBlinds(roomName, SOURCE_SCHEDULE);
            case SET_BATTERY_TEMPERATURE -> {
                Integer targetValue = schedule.getTargetValue();
                if (targetValue == null) {
                    log.warn("Пропуск расписания {}: отсутствует targetValue для батареи", schedule.getId());
                    return false;
                }
                setBatteryTemperature(roomName, targetValue, SOURCE_SCHEDULE);
            }
            default -> {
                log.warn("Неизвестный тип действия расписания {}: {}", schedule.getId(), schedule.getActionType());
                return false;
            }
        }

        schedule.setLastExecutedAt(executionTime);
        scheduleRepository.save(schedule);
        return true;
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.findById(id).ifPresent(schedule -> {
            scheduleRepository.delete(schedule);
            log.info("Удалено расписание с id: {}", id);
        });
    }

    public RoomEntity createRoomWithDevices(String roomName) {
        if (roomRepository.findByName(roomName).isPresent()) {
            log.warn("Попытка создать уже существующую комнату: {}", roomName);
            throw new RuntimeException("Комната '" + roomName + "' уже существует");
        }

        RoomEntity room = new RoomEntity();
        room.setName(roomName);
        RoomEntity savedRoom = roomRepository.save(room);

        BatteryEntity battery = new BatteryEntity();
        battery.setTemperature(20);
        battery.setLastUpdated(LocalDateTime.now());
        battery.setRoom(savedRoom);
        batteryRepository.save(battery);

        BlindsEntity blinds = new BlindsEntity();
        blinds.setPosition(50);
        blinds.setLastUpdated(LocalDateTime.now());
        blinds.setRoom(savedRoom);
        blindsRepository.save(blinds);

        log.info("Создана комната {} с батареей (20°) и жалюзи (50%)", roomName);
        return savedRoom;
    }

    public List<RoomEntity> getAllRooms() {
        return roomRepository.findAll();
    }
}