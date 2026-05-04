package ru.tbank.practicum.smarthome;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class SmartHomeApplication {
    @Bean
    public CommandLineRunner checkMetrics(MeterRegistry registry) {
        return args -> {
            System.out.println("=== MeterRegistry beans ===");
            registry.getMeters().forEach(m -> System.out.println(m.getId().getName()));
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(SmartHomeApplication.class, args);

    }
}


