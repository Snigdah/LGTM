package com.example.payment.controller;

import com.example.payment.service.PaymentProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Called by App 1 over Feign:
 *   GET /api/payment/charge?customer=...&amount=...
 */
@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentProcessingService service;

    public PaymentController(PaymentProcessingService service) {
        this.service = service;
    }

    @GetMapping("/charge")
    public Map<String, Object> charge(
            @RequestParam(defaultValue = "guest") String customer,
            @RequestParam(defaultValue = "100") double amount) {
        log.info("GET /api/payment/charge customer={} amount={}", customer, amount);
        return service.charge(customer, amount);
    }
}
