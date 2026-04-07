package ru.tbank.practicum.smarthome.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.practicum.smarthome.controller.dto.ScheduleDto;
import ru.tbank.practicum.smarthome.entity.ScheduleEntity;
import ru.tbank.practicum.smarthome.service.SmartHomeService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedules")
public class ScheduleController {

    private final SmartHomeService smartHomeService;

    public ScheduleController(SmartHomeService smartHomeService) {
        this.smartHomeService = smartHomeService;
    }

    @PostMapping
    public ScheduleDto createSchedule(
            @RequestParam String time,
            @RequestParam String room,
            @RequestParam String action) {
        ScheduleEntity task = smartHomeService.addSchedule(time, room, action);
        return new ScheduleDto(task.getId(), task.getTime(), task.getRoom().getName(), task.getAction());
    }

    @GetMapping
    public List<ScheduleDto> getAllSchedules() {
        List<ScheduleEntity> tasks = smartHomeService.getAllSchedules();
        return tasks.stream()
                .map(task -> new ScheduleDto(task.getId(), task.getTime(), task.getRoom().getName(), task.getAction()))
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public void deleteSchedule(@PathVariable Long id) {
        smartHomeService.deleteSchedule(id);
    }
}