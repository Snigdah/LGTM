package com.example.demo.controller;

import com.example.demo.service.CheckoutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Multi-step request flow for tracing demos.
 *
 *   GET /api/checkout?customer=alice&amount=250    -> passes all 3 steps
 *   GET /api/checkout?customer=bob&amount=9999     -> fails at step 3 (payment)
 */
@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private static final Logger log = LoggerFactory.getLogger(CheckoutController.class);

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
    }

    @GetMapping
    public Map<String, Object> checkout(
            @RequestParam(defaultValue = "guest") String customer,
            @RequestParam(defaultValue = "100") double amount) {
        log.info("GET /api/checkout customer={} amount={}", customer, amount);
        return checkoutService.process(customer, amount);
    }
}
