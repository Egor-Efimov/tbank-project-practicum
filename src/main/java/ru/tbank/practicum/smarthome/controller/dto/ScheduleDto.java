package ru.tbank.practicum.smarthome.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleDto {
    private Long id;
    private String time;
    private String room;
    private String actionType;
    private Integer targetValue;
    private Boolean enabled;
}
