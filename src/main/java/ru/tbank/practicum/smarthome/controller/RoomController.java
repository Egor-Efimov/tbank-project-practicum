package ru.tbank.practicum.smarthome.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.practicum.smarthome.controller.dto.RoomDto;
import ru.tbank.practicum.smarthome.entity.RoomEntity;
import ru.tbank.practicum.smarthome.service.SmartHomeService;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final SmartHomeService smartHomeService;

    public RoomController(SmartHomeService smartHomeService) {
        this.smartHomeService = smartHomeService;
    }

    @PostMapping("/{name}")
    public RoomDto createRoom(@PathVariable String name) {
        RoomEntity room = smartHomeService.createRoomWithDevices(name);
        return new RoomDto(room.getId(), room.getName());
    }

    @GetMapping
    public List<RoomDto> getAllRooms() {
        return smartHomeService.getAllRooms().stream()
                .map(room -> new RoomDto(room.getId(), room.getName()))
                .collect(Collectors.toList());
    }
}
