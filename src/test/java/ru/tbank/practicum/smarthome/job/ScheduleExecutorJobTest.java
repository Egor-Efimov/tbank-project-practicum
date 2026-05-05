package ru.tbank.practicum.smarthome.job;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.practicum.smarthome.entity.RoomEntity;
import ru.tbank.practicum.smarthome.entity.ScheduleActionType;
import ru.tbank.practicum.smarthome.entity.ScheduleEntity;
import ru.tbank.practicum.smarthome.repository.ScheduleRepository;
import ru.tbank.practicum.smarthome.service.SmartHomeService;

@ExtendWith(MockitoExtension.class)
class ScheduleExecutorJobTest {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private SmartHomeService smartHomeService;

    @InjectMocks
    private ScheduleExecutorJob scheduleExecutorJob;

    @Test
    void executeSchedules_whenTimeMatches_thenExecutesRule() {
        RoomEntity room = new RoomEntity();
        room.setName("kitchen");

        ScheduleEntity schedule = new ScheduleEntity();
        schedule.setId(1L);
        schedule.setRoom(room);
        schedule.setEnabled(true);
        schedule.setActionType(ScheduleActionType.OPEN_BLINDS);
        schedule.setTime(LocalDateTime.now().format(TIME_FORMATTER));

        when(scheduleRepository.findByEnabledTrue()).thenReturn(List.of(schedule));
        when(smartHomeService.executeScheduleAction(any(ScheduleEntity.class), any(LocalDateTime.class)))
                .thenReturn(true);

        scheduleExecutorJob.executeSchedules();

        verify(smartHomeService).executeScheduleAction(any(ScheduleEntity.class), any(LocalDateTime.class));
    }

    @Test
    void executeSchedules_whenAlreadyExecutedThisMinute_thenSkips() {
        RoomEntity room = new RoomEntity();
        room.setName("kitchen");

        ScheduleEntity schedule = new ScheduleEntity();
        schedule.setId(2L);
        schedule.setRoom(room);
        schedule.setEnabled(true);
        schedule.setActionType(ScheduleActionType.CLOSE_BLINDS);
        schedule.setTime(LocalDateTime.now().format(TIME_FORMATTER));
        schedule.setLastExecutedAt(LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));

        when(scheduleRepository.findByEnabledTrue()).thenReturn(List.of(schedule));

        scheduleExecutorJob.executeSchedules();

        verify(smartHomeService, never()).executeScheduleAction(any(ScheduleEntity.class), any(LocalDateTime.class));
    }
}
