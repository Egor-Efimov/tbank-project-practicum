package ru.tbank.practicum.smarthome.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.practicum.smarthome.entity.ScheduleEntity;

public interface ScheduleRepository extends JpaRepository<ScheduleEntity, Long> {
    List<ScheduleEntity> findByRoomId(Long roomId);

    List<ScheduleEntity> findByEnabledTrue();
}
