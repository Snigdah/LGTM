package com.example.demo.service;

import com.example.demo.client.PaymentClient;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Checkout that calls the REMOTE payment-service (App 2) over Feign.
 *
 * Trace shape across TWO services:
 *
 *   springboot-demo:  GET /api/checkout-remote
 *     checkout-remote                       (this method)
 *       reserve-inventory                   (local, App 1)
 *       GET /api/payment/charge             (Feign client span, App 1)
 *   payment-service:  GET /api/payment/charge   (server span, App 2)
 *     process-payment                       (App 2 method - passes or fails here)
 */
@Service
public class RemoteCheckoutService {

    private static final Logger log = LoggerFactory.getLogger(RemoteCheckoutService.class);

    private final InventoryService inventoryService;
    private final PaymentClient paymentClient;

    public RemoteCheckoutService(InventoryService inventoryService, PaymentClient paymentClient) {
        this.inventoryService = inventoryService;
        this.paymentClient = paymentClient;
    }

    @Observed(name = "checkout.remote", contextualName = "checkout-remote")
    public Map<String, Object> process(String customer, double amount) {
        log.info("App1: remote checkout for customer={} amount={}", customer, amount);

        // Step 1 (local): reserve stock
        inventoryService.reserve(customer);

        // Step 2 (remote): call App 2 over Feign -> continues the same trace
        log.info("App1: calling payment-service for customer={}", customer);
        Map<String, Object> payment = paymentClient.charge(customer, amount);
        log.info("App1: payment-service replied status={}", payment.get("status"));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "CONFIRMED");
        result.put("customer", customer);
        result.put("amount", amount);
        result.put("payment", payment);
        result.put("handledBy", "springboot-demo -> payment-service");
        result.put("time", Instant.now().toString());
        return result;
    }
}
