package ru.tbank.practicum.smarthome.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tbank.practicum.smarthome.controller.dto.BlindsDto;
import ru.tbank.practicum.smarthome.service.SmartHomeService;

@RestController
@RequestMapping("/api/blinds")
public class BlindsController {

    private final SmartHomeService smartHomeService;

    public BlindsController(SmartHomeService smartHomeService) {
        this.smartHomeService = smartHomeService;
    }

    // получить положение жалюзи
    @GetMapping("/{room}")
    public BlindsDto getBlindsPosition(@PathVariable String room) {
        int position = smartHomeService.getBlindsPosition(room);
        return new BlindsDto(room, position);
    }

    // установить положение на указанный процент
    @PostMapping("/{room}")
    public BlindsDto setBlindsPosition(@PathVariable String room, @RequestParam int position) {
        int newPosition = smartHomeService.setBlindsPosition(room, position);
        return new BlindsDto(room, newPosition);
    }

    @PostMapping("/{room}/open")
    public BlindsDto openBlinds(@PathVariable String room) {
        smartHomeService.openBlinds(room);
        return new BlindsDto(room, smartHomeService.getBlindsPosition(room));
    }

    @PostMapping("/{room}/close")
    public BlindsDto closeBlinds(@PathVariable String room) {
        smartHomeService.closeBlinds(room);
        return new BlindsDto(room, smartHomeService.getBlindsPosition(room));
    }
}
