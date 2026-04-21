package ru.tbank.practicum.smarthome.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tbank.practicum.smarthome.entity.BatteryEntity;
import ru.tbank.practicum.smarthome.entity.BlindsEntity;
import ru.tbank.practicum.smarthome.entity.RoomEntity;
import ru.tbank.practicum.smarthome.entity.ScheduleEntity;
import ru.tbank.practicum.smarthome.repository.ActionLogRepository;
import ru.tbank.practicum.smarthome.repository.BatteryRepository;
import ru.tbank.practicum.smarthome.repository.BlindsRepository;
import ru.tbank.practicum.smarthome.repository.RoomRepository;
import ru.tbank.practicum.smarthome.repository.ScheduleRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmartHomeServiceTest {

    @Mock
    private BatteryRepository batteryRepository;

    @Mock
    private BlindsRepository blindsRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private ActionLogRepository actionLogRepository;

    @InjectMocks
    private SmartHomeService smartHomeService;

    @Test
    void setBatteryTemperature_whenRoomExists_thenSuccess() {
        long roomId = 1;
        String roomName = "kitchen";
        int newTemp = 25;

        RoomEntity room = new RoomEntity();
        room.setId(roomId);
        room.setName(roomName);

        BatteryEntity battery = new BatteryEntity();
        battery.setId(1L);
        battery.setTemperature(24);
        battery.setRoom(room);

        when(roomRepository.findByName(roomName)).thenReturn(Optional.of(room));
        when(batteryRepository.findByRoomId(roomId)).thenReturn(Optional.of(battery));

        int result = smartHomeService.setBatteryTemperature(roomName, newTemp);

        assertThat(result).isEqualTo(newTemp);
        assertThat(battery.getTemperature()).isEqualTo(newTemp);
        verify(batteryRepository).save(battery);
        verify(actionLogRepository).save(any());
    }

    @Test
    void setBatteryTemperature_whenRoomNotFound_thenThrowException() {
        String roomName = "test_room";

        when(roomRepository.findByName(roomName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> smartHomeService.setBatteryTemperature(roomName, 25))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Комната 'test_room' не найдена");

        verify(batteryRepository, never()).save(any());
    }

    @Test
    void getBatteryTemperature_whenRoomExists_thenReturnTemperature() {
        long roomId = 1;
        String roomName = "kitchen";

        RoomEntity room = new RoomEntity();
        room.setId(roomId);
        room.setName(roomName);

        BatteryEntity battery = new BatteryEntity();
        battery.setTemperature(22);
        battery.setRoom(room);

        when(roomRepository.findByName(roomName)).thenReturn(Optional.of(room));
        when(batteryRepository.findByRoomId(roomId)).thenReturn(Optional.of(battery));

        int result = smartHomeService.getBatteryTemperature(roomName);

        assertThat(result).isEqualTo(22);
    }

    @Test
    void getBatteryTemperature_whenRoomNotFound_thenThrowException() {
        String roomName = "test_room";

        when(roomRepository.findByName(roomName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> smartHomeService.getBatteryTemperature(roomName))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Комната 'test_room' не найдена");
    }

    @Test
    void setBlindsPosition_whenRoomExists_thenSuccess() {
        long roomId = 1;
        String roomName = "kitchen";
        int newPosition = 80;

        RoomEntity room = new RoomEntity();
        room.setId(roomId);
        room.setName(roomName);

        BlindsEntity blinds = new BlindsEntity();
        blinds.setId(1L);
        blinds.setPosition(50);
        blinds.setRoom(room);

        when(roomRepository.findByName(roomName)).thenReturn(Optional.of(room));
        when(blindsRepository.findByRoomId(roomId)).thenReturn(Optional.of(blinds));

        int result = smartHomeService.setBlindsPosition(roomName, newPosition);

        assertThat(result).isEqualTo(newPosition);
        assertThat(blinds.getPosition()).isEqualTo(newPosition);
        verify(blindsRepository).save(blinds);
        verify(actionLogRepository).save(any());
    }

    @Test
    void setBlindsPosition_whenRoomNotFound_thenThrowException() {
        String roomName = "test_room";

        when(roomRepository.findByName(roomName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> smartHomeService.setBlindsPosition(roomName, 50))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Комната 'test_room' не найдена");
    }

    @Test
    void openBlinds_whenRoomExists_thenSetPositionTo100() {
        long roomId = 1;
        String roomName = "kitchen";

        RoomEntity room = new RoomEntity();
        room.setId(roomId);
        room.setName(roomName);

        BlindsEntity blinds = new BlindsEntity();
        blinds.setId(1L);
        blinds.setPosition(0);
        blinds.setRoom(room);

        when(roomRepository.findByName(roomName)).thenReturn(Optional.of(room));
        when(blindsRepository.findByRoomId(roomId)).thenReturn(Optional.of(blinds));

        smartHomeService.openBlinds(roomName);

        assertThat(blinds.getPosition()).isEqualTo(100);
        verify(blindsRepository).save(blinds);
    }

    @Test
    void closeBlinds_whenRoomExists_thenSetPositionTo0() {
        long roomId = 1;
        String roomName = "kitchen";

        RoomEntity room = new RoomEntity();
        room.setId(roomId);
        room.setName(roomName);

        BlindsEntity blinds = new BlindsEntity();
        blinds.setId(1L);
        blinds.setPosition(100);
        blinds.setRoom(room);

        when(roomRepository.findByName(roomName)).thenReturn(Optional.of(room));
        when(blindsRepository.findByRoomId(roomId)).thenReturn(Optional.of(blinds));

        smartHomeService.closeBlinds(roomName);

        assertThat(blinds.getPosition()).isEqualTo(0);
        verify(blindsRepository).save(blinds);
    }

    @Test
    void addSchedule_whenRoomExists_thenSuccess() {
        long roomId = 1;
        String roomName = "kitchen";
        String time = "06:00";
        String action = "OPEN";

        RoomEntity room = new RoomEntity();
        room.setId(roomId);
        room.setName(roomName);

        when(roomRepository.findByName(roomName)).thenReturn(Optional.of(room));
        when(scheduleRepository.save(any(ScheduleEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ScheduleEntity result = smartHomeService.addSchedule(time, roomName, action);

        assertThat(result.getTime()).isEqualTo(time);
        assertThat(result.getAction()).isEqualTo("OPEN");
        assertThat(result.getEnabled()).isTrue();
        assertThat(result.getRoom()).isEqualTo(room);
        verify(scheduleRepository).save(any(ScheduleEntity.class));
        verify(actionLogRepository).save(any());
    }

    @Test
    void addSchedule_whenRoomNotFound_thenThrowException() {
        String roomName = "test_room";

        when(roomRepository.findByName(roomName)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> smartHomeService.addSchedule("06:00", roomName, "OPEN"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Комната 'test_room' не найдена");
    }

    @Test
    void getAllSchedules_returnsList() {
        when(scheduleRepository.findAll()).thenReturn(List.of());

        List<ScheduleEntity> result = smartHomeService.getAllSchedules();

        assertThat(result).isEmpty();
        verify(scheduleRepository).findAll();
    }

    @Test
    void deleteSchedule_whenExists_thenDeletes() {
        long scheduleId = 1;
        ScheduleEntity schedule = new ScheduleEntity();
        schedule.setId(scheduleId);

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));

        smartHomeService.deleteSchedule(scheduleId);

        verify(scheduleRepository).delete(schedule);
    }

    @Test
    void deleteSchedule_whenNotExists_thenDoesNothing() {
        long scheduleId = 999;

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        smartHomeService.deleteSchedule(scheduleId);

        verify(scheduleRepository, never()).delete(any());
    }
}