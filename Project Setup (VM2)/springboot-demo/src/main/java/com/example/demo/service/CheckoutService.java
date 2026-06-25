package com.example.demo.service;

import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Step 1 / orchestrator of the checkout chain.
 * Trace shape produced for one request:
 *
 *   GET /api/checkout                 (HTTP server span, automatic)
 *     checkout-process                (this method)
 *       reserve-inventory             (InventoryService - step 2)
 *       charge-payment                (PaymentService  - step 3, may fail)
 */
@Service
public class CheckoutService {

    private static final Logger log = LoggerFactory.getLogger(CheckoutService.class);

    private final InventoryService inventoryService;
    private final PaymentService paymentService;

    public CheckoutService(InventoryService inventoryService, PaymentService paymentService) {
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
    }

    @Observed(name = "checkout.process", contextualName = "checkout-process")
    public Map<String, Object> process(String customer, double amount) {
        log.info("Step 1/3 - validating checkout for customer={} amount={}", customer, amount);
        sleep(20, 60);

        // Step 2: reserve stock
        inventoryService.reserve(customer);

        // Step 3: charge payment (throws PaymentDeclinedException if amount > limit)
        String txn = paymentService.charge(customer, amount);

        log.info("Checkout CONFIRMED for customer={} txn={}", customer, txn);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "CONFIRMED");
        result.put("customer", customer);
        result.put("amount", amount);
        result.put("transaction", txn);
        result.put("time", Instant.now().toString());
        return result;
    }

    private void sleep(int minMs, int maxMs) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(minMs, maxMs));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
