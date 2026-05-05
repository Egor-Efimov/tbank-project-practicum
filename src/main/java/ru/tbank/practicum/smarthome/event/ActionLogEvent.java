package ru.tbank.practicum.smarthome.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionLogEvent {
    private String eventId;
    private String timestamp;
    private String deviceType;
    private String action;
    private String room;
    private Integer oldValue;
    private Integer newValue;
    private String source;
}
