package ru.tbank.practicum.smarthome.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.tbank.practicum.smarthome.entity.ActionLogEntity;
import ru.tbank.practicum.smarthome.entity.RoomEntity;
import ru.tbank.practicum.smarthome.event.ActionLogEvent;
import ru.tbank.practicum.smarthome.repository.ActionLogRepository;
import ru.tbank.practicum.smarthome.repository.RoomRepository;

import java.time.LocalDateTime;

@Component
public class ActionLogEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(ActionLogEventConsumer.class);

    private final ActionLogRepository actionLogRepository;
    private final RoomRepository roomRepository;

    public ActionLogEventConsumer(ActionLogRepository actionLogRepository, RoomRepository roomRepository) {
        this.actionLogRepository = actionLogRepository;
        this.roomRepository = roomRepository;
    }

    @KafkaListener(topics = "action-logs", groupId = "smarthome-group")
    public void consume(ActionLogEvent event) {
        RoomEntity room = roomRepository.findByName(event.getRoom()).orElse(null);

        ActionLogEntity logEntity = new ActionLogEntity();
        logEntity.setTimestamp(LocalDateTime.parse(event.getTimestamp()));
        logEntity.setDeviceType(event.getDeviceType());
        logEntity.setAction(event.getAction());
        logEntity.setOldValue(event.getOldValue());
        logEntity.setNewValue(event.getNewValue());
        logEntity.setSource(event.getSource());
        logEntity.setRoom(room);

        actionLogRepository.save(logEntity);
        log.info("Сохранено действие пользователя из Kafka: {} {}", event.getDeviceType(), event.getAction());
    }
}