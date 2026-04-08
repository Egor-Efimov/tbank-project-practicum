package ru.tbank.practicum.smarthome.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.tbank.practicum.smarthome.entity.ActionLogEntity;

public interface ActionLogRepository extends JpaRepository<ActionLogEntity, Long> {}
