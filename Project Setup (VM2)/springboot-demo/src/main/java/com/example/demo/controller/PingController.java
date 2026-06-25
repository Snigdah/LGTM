package com.example.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class PingController {

    private static final Logger log = LoggerFactory.getLogger(PingController.class);

    /** GET /api/ping -> trivial liveness check that also emits a log line */
    @GetMapping("/ping")
    public Map<String, Object> ping() {
        log.info("GET /api/ping");
        return Map.of(
                "status", "ok",
                "service", "springboot-demo",
                "time", Instant.now().toString()
        );
    }
}
