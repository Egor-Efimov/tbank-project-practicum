package ru.tbank.practicum.smarthome.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
        int newTemp = smartHomeService.setBatteryTemperature(room, temp);
        return new BatteryDto(room, newTemp);
    }
}
