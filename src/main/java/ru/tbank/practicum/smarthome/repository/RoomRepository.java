package ru.tbank.practicum.smarthome.repository;

import ru.tbank.practicum.smarthome.entity.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<RoomEntity, Long> {
    Optional<RoomEntity> findByName(String name);
}