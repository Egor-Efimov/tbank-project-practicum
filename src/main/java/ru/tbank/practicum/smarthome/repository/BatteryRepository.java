package ru.tbank.practicum.smarthome.repository;

import ru.tbank.practicum.smarthome.entity.BatteryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BatteryRepository extends JpaRepository<BatteryEntity, Long> {
    Optional<BatteryEntity> findByRoomId(Long roomId);
}