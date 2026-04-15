package ru.tbank.practicum.smarthome.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.practicum.smarthome.entity.BatteryEntity;

public interface BatteryRepository extends JpaRepository<BatteryEntity, Long> {
    Optional<BatteryEntity> findByRoomId(Long roomId);
}
