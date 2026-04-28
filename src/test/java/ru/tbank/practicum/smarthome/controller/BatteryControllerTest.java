package ru.tbank.practicum.smarthome.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tbank.practicum.smarthome.service.SmartHomeService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BatteryController.class)
class BatteryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SmartHomeService smartHomeService;

    @Test
    void getBatteryTemperature_test() throws Exception {
        String roomName = "kitchen";
        int temperature = 23;

        when(smartHomeService.getBatteryTemperature(roomName)).thenReturn(temperature);

        mockMvc.perform(get("/api/batteries/{room}", roomName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.room").value(roomName))
                .andExpect(jsonPath("$.temperature").value(temperature));
    }

    @Test
    void setBatteryTemperature_test() throws Exception {
        String roomName = "kitchen";
        int newTemp = 25;

        when(smartHomeService.setBatteryTemperature(roomName, newTemp)).thenReturn(newTemp);

        mockMvc.perform(post("/api/batteries/{room}?temp={temp}", roomName, newTemp))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.room").value(roomName))
                .andExpect(jsonPath("$.temperature").value(newTemp));
    }
}