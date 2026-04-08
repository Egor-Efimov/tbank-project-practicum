package ru.tbank.practicum.smarthome.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.practicum.smarthome.entity.WeatherLogEntity;

public interface WeatherLogRepository extends JpaRepository<WeatherLogEntity, Long> {
    Optional<WeatherLogEntity> findTopByOrderByTimestampDesc();
}
