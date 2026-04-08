package ru.tbank.practicum.smarthome.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.practicum.smarthome.entity.BlindsEntity;

public interface BlindsRepository extends JpaRepository<BlindsEntity, Long> {
    Optional<BlindsEntity> findByRoomId(Long roomId);
}
