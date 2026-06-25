package com.example.payment.service;

import com.example.payment.exception.PaymentDeclinedException;
import io.micrometer.observation.annotation.Observed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Core logic of App 2. Runs inside the SAME trace as App 1 (the trace context
 * arrives over the Feign HTTP call), so this method shows up as a span under
 * the "payment-service" service in Tempo.
 */
@Service
public class PaymentProcessingService {

    private static final Logger log = LoggerFactory.getLogger(PaymentProcessingService.class);
    private static final double LIMIT = 5000.0;

    @Observed(name = "payment.process", contextualName = "process-payment")
    public Map<String, Object> charge(String customer, double amount) {
        log.info("payment-service: processing charge customer={} amount={}", customer, amount);
        sleep(40, 120);

        if (amount > LIMIT) {
            log.error("payment-service: DECLINED customer={} amount={} (limit={})", customer, amount, LIMIT);
            throw new PaymentDeclinedException(customer, amount);
        }

        String txn = "TXN-" + System.currentTimeMillis();
        log.info("payment-service: APPROVED customer={} txn={}", customer, txn);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", "APPROVED");
        result.put("customer", customer);
        result.put("amount", amount);
        result.put("transaction", txn);
        result.put("processedBy", "payment-service");
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
