package com.example.demo.controller;

import com.example.demo.service.RemoteCheckoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Cross-service demo endpoint (App 1 -> App 2 via Feign).
 *
 *   GET /api/checkout-remote?customer=alice&amount=250    -> passes (App 2 approves)
 *   GET /api/checkout-remote?customer=bob&amount=9999      -> fails in App 2 (declined)
 */
@RestController
@RequestMapping("/api/checkout-remote")
public class RemoteCheckoutController {

    private static final Logger log = LoggerFactory.getLogger(RemoteCheckoutController.class);

    private final RemoteCheckoutService remoteCheckoutService;

    public RemoteCheckoutController(RemoteCheckoutService remoteCheckoutService) {
        this.remoteCheckoutService = remoteCheckoutService;
    }

    @GetMapping
    public Map<String, Object> checkout(
            @RequestParam(defaultValue = "guest") String customer,
            @RequestParam(defaultValue = "100") double amount) {
        log.info("GET /api/checkout-remote customer={} amount={}", customer, amount);
        return remoteCheckoutService.process(customer, amount);
    }
}
