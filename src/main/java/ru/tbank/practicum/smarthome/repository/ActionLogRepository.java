package ru.tbank.practicum.smarthome.repository;

import ru.tbank.practicum.smarthome.entity.ActionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionLogRepository extends JpaRepository<ActionLogEntity, Long> {
}