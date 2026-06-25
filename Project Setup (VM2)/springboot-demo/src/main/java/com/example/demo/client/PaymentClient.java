package com.example.demo.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * Feign client that calls App 2 (payment-service).
 * The base URL comes from application.yml (payment.service.url) and points at
 * the App 2 container on the shared Docker network.
 *
 * Trace context is propagated automatically on this call, so App 2's spans
 * join the same trace as App 1.
 */
@FeignClient(name = "payment-service", url = "${payment.service.url}")
public interface PaymentClient {

    @GetMapping("/api/payment/charge")
    Map<String, Object> charge(@RequestParam("customer") String customer,
                               @RequestParam("amount") double amount);
}
