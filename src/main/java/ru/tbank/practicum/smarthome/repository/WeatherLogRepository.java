package ru.tbank.practicum.smarthome.repository;

import ru.tbank.practicum.smarthome.entity.WeatherLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface WeatherLogRepository extends JpaRepository<WeatherLogEntity, Long> {
    Optional<WeatherLogEntity> findTopByOrderByTimestampDesc();
}