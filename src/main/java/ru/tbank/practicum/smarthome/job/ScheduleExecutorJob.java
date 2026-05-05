package ru.tbank.practicum.smarthome.job;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.tbank.practicum.smarthome.entity.ScheduleEntity;
import ru.tbank.practicum.smarthome.repository.ScheduleRepository;
import ru.tbank.practicum.smarthome.service.SmartHomeService;

@Component
public class ScheduleExecutorJob {

    private static final Logger log = LoggerFactory.getLogger(ScheduleExecutorJob.class);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ScheduleRepository scheduleRepository;
    private final SmartHomeService smartHomeService;

    public ScheduleExecutorJob(ScheduleRepository scheduleRepository, SmartHomeService smartHomeService) {
        this.scheduleRepository = scheduleRepository;
        this.smartHomeService = smartHomeService;
    }

    @Scheduled(cron = "0 * * * * *")
    public void executeSchedules() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        String currentTime = now.format(TIME_FORMATTER);

        List<ScheduleEntity> schedules = scheduleRepository.findByEnabledTrue();
        for (ScheduleEntity schedule : schedules) {
            if (!currentTime.equals(schedule.getTime())) {
                continue;
            }

            LocalDateTime lastExecutedAt = schedule.getLastExecutedAt();
            if (lastExecutedAt != null
                    && lastExecutedAt.truncatedTo(ChronoUnit.MINUTES).isEqual(now)) {
                continue;
            }

            if (!isValidScheduleTime(schedule.getTime(), schedule.getId())) {
                continue;
            }

            boolean applied = smartHomeService.executeScheduleAction(schedule, now);
            if (applied) {
                log.info(
                        "Расписание {} выполнено: room={}, action={}, time={}",
                        schedule.getId(),
                        schedule.getRoom().getName(),
                        schedule.getActionType(),
                        currentTime);
            }
        }
    }

    private boolean isValidScheduleTime(String time, Long scheduleId) {
        try {
            TIME_FORMATTER.parse(time);
            return true;
        } catch (DateTimeParseException ex) {
            log.warn("Некорректный формат времени в расписании {}: {}", scheduleId, time);
            return false;
        }
    }
}
