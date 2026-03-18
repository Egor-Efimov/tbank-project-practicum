package ru.tbank.practicum.smarthome.controller;

import org.springframework.web.bind.annotation.*;
import ru.tbank.practicum.smarthome.controller.dto.BatteryDto;
import ru.tbank.practicum.smarthome.service.SmartHomeService;

@RestController
@RequestMapping("/api/batteries")
public class BatteryController {
    private final SmartHomeService smartHomeService;

    public BatteryController(SmartHomeService smartHomeService) {
        this.smartHomeService = smartHomeService;
    }

    @GetMapping("/{room}")
    public BatteryDto getBatteryTemperature(@PathVariable String room) {
        int temperature = smartHomeService.getBatteryTemperature(room);

        BatteryDto response = new BatteryDto(room, temperature);

        return response;
    }

    @PostMapping("/{room}")
    public BatteryDto setBatteryTemperature(@PathVariable String room, @RequestParam int temp) {
        smartHomeService.setBatteryTemperature(room, temp);

        int newTemp = smartHomeService.getBatteryTemperature(room);

        return new BatteryDto(room, newTemp);
    }
}
