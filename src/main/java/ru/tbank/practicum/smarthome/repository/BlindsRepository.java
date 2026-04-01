package ru.tbank.practicum.smarthome.repository;

import ru.tbank.practicum.smarthome.entity.BlindsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface BlindsRepository extends JpaRepository<BlindsEntity, Long> {
    Optional<BlindsEntity> findByRoomId(Long roomId);
}