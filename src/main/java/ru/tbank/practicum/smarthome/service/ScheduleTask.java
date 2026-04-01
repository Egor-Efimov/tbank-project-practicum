package ru.tbank.practicum.smarthome.service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ScheduleTask {
    private Long id;
    private String time;
    private String room;
    private String action;
    private boolean enabled;
}
