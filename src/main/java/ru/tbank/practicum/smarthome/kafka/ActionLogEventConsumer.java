package ru.tbank.practicum.smarthome.kafka;

import java.time.LocalDateTime;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.tbank.practicum.smarthome.entity.ActionLogEntity;
import ru.tbank.practicum.smarthome.entity.RoomEntity;
import ru.tbank.practicum.smarthome.event.ActionLogEvent;
import ru.tbank.practicum.smarthome.repository.ActionLogRepository;
import ru.tbank.practicum.smarthome.repository.RoomRepository;

@Component
public class ActionLogEventConsumer {
    private final ActionLogRepository actionLogRepository;
    private final RoomRepository roomRepository;

    public ActionLogEventConsumer(ActionLogRepository actionLogRepository, RoomRepository roomRepository) {
        this.actionLogRepository = actionLogRepository;
        this.roomRepository = roomRepository;
    }

    @KafkaListener(topics = "action-logs", groupId = "smarthome-group")
    public void consume(ActionLogEvent event) {
        RoomEntity room = roomRepository.findByName(event.getRoom()).orElse(null);

        ActionLogEntity log = new ActionLogEntity();
        log.setTimestamp(LocalDateTime.parse(event.getTimestamp()));
        log.setDeviceType(event.getDeviceType());
        log.setAction(event.getAction());
        log.setOldValue(event.getOldValue());
        log.setNewValue(event.getNewValue());
        log.setSource(event.getSource());
        log.setRoom(room);

        actionLogRepository.save(log);
        System.out.println(
                "Сохранено действие пользователя из Kafka: " + event.getDeviceType() + " " + event.getAction());
    }
}
