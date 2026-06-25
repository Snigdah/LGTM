package com.example.demo.service;

import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Step 2 of the checkout chain. Reserves stock for the order.
 * Each call becomes a "reserve-inventory" span nested under checkout-process.
 */
@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    @Observed(name = "inventory.reserve", contextualName = "reserve-inventory")
    public void reserve(String customer) {
        log.info("Step 2/3 - reserving inventory for customer={}", customer);
        sleep(30, 90);
        log.info("Step 2/3 - inventory reserved for customer={}", customer);
    }

    private void sleep(int minMs, int maxMs) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextLong(minMs, maxMs));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
